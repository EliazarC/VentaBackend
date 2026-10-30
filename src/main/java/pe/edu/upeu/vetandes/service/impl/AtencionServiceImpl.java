package pe.edu.upeu.vetandes.service.impl;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.vetandes.dto.AnularAtencionRequestDTO;
import pe.edu.upeu.vetandes.dto.AtencionRequestDTO;
import pe.edu.upeu.vetandes.dto.AtencionResponseDTO;
import pe.edu.upeu.vetandes.entity.Atencion;
import pe.edu.upeu.vetandes.entity.DetalleAtencion;
import pe.edu.upeu.vetandes.entity.Mascota;
import pe.edu.upeu.vetandes.entity.Prestacion;
import pe.edu.upeu.vetandes.exception.ApiException;
import pe.edu.upeu.vetandes.mapper.AtencionMapper;
import pe.edu.upeu.vetandes.repository.AtencionRepository;
import pe.edu.upeu.vetandes.repository.MascotaRepository;
import pe.edu.upeu.vetandes.repository.PrestacionRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AtencionServiceImpl implements pe.edu.upeu.vetandes.service.AtencionService {
    private static final Logger log = LoggerFactory.getLogger(AtencionServiceImpl.class);
    private static final BigDecimal TASA_DESCUENTO = new BigDecimal("0.10");
    private final AtencionRepository atencionRepository;
    private final MascotaRepository mascotaRepository;
    private final PrestacionRepository prestacionRepository;
    private final EntityManager entityManager;

    public AtencionServiceImpl(AtencionRepository atencionRepository, MascotaRepository mascotaRepository,
                               PrestacionRepository prestacionRepository, EntityManager entityManager) {
        this.atencionRepository = atencionRepository;
        this.mascotaRepository = mascotaRepository;
        this.prestacionRepository = prestacionRepository;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public AtencionResponseDTO registrar(AtencionRequestDTO request, String usuarioRegistro) {
        Mascota mascota = mascotaRepository.findById(request.mascotaId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Mascota no encontrada"));

        Set<Long> idsPrestaciones = new HashSet<>();
        for (var detalleRequest : request.detalles()) {
            if (!idsPrestaciones.add(detalleRequest.prestacionId())) {
                log.warn("Registro de atención rechazado: prestación repetida {}", detalleRequest.prestacionId());
                throw new ApiException(HttpStatus.BAD_REQUEST, "No se puede repetir una prestación en la misma atención");
            }
        }

        Atencion atencion = new Atencion();
        atencion.setNumero(siguienteNumero());
        atencion.setFecha(LocalDateTime.now());
        atencion.setMascota(mascota);
        atencion.setPeso(request.peso());
        atencion.setDiagnostico(request.diagnostico());
        atencion.setEstado("REGISTRADA");
        atencion.setUsuarioRegistro(usuarioRegistro);

        BigDecimal subtotal = BigDecimal.ZERO;
        for (var detalleRequest : request.detalles()) {
            Prestacion prestacion = prestacionRepository.findById(detalleRequest.prestacionId())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                            "No existe la prestación " + detalleRequest.prestacionId()));
            boolean especieValida = "TODAS".equals(prestacion.getEspecieAplicable())
                    || mascota.getEspecie().equals(prestacion.getEspecieAplicable());
            if (!Boolean.TRUE.equals(prestacion.getActivo()) || !especieValida) {
                log.warn("Detalle rechazado en atención: prestación {} inactiva o incompatible con especie {}",
                        prestacion.getId(), mascota.getEspecie());
                throw new ApiException(HttpStatus.CONFLICT,
                        "La prestación " + prestacion.getCodigo() + " está inactiva o no aplica a la especie " + mascota.getEspecie());
            }
            BigDecimal precioUnitario = prestacion.getPrecio();
            BigDecimal subtotalLinea = precioUnitario.multiply(BigDecimal.valueOf(detalleRequest.cantidad()))
                    .setScale(2, RoundingMode.HALF_UP);
            DetalleAtencion detalle = new DetalleAtencion();
            detalle.setPrestacion(prestacion);
            detalle.setCantidad(detalleRequest.cantidad());
            detalle.setPrecioUnitario(precioUnitario);
            detalle.setSubtotal(subtotalLinea);
            atencion.agregarDetalle(detalle);
            subtotal = subtotal.add(subtotalLinea);
        }

        subtotal = subtotal.setScale(2, RoundingMode.HALF_UP);
        BigDecimal descuento = request.detalles().size() >= 3
                ? subtotal.multiply(TASA_DESCUENTO).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        atencion.setSubtotal(subtotal);
        atencion.setDescuento(descuento);
        atencion.setTotal(subtotal.subtract(descuento).setScale(2, RoundingMode.HALF_UP));

        Atencion guardada = atencionRepository.saveAndFlush(atencion);
        log.info("Atención {} registrada con {} detalles, total {}", guardada.getNumero(),
                guardada.getDetalles().size(), guardada.getTotal());
        return AtencionMapper.toResponse(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AtencionResponseDTO> consultar(String mascota, String especie, String estado,
                                                LocalDate desde, LocalDate hasta) {
        validarRango(desde, hasta);
        LocalDateTime fechaDesde = desde == null ? null : desde.atStartOfDay();
        LocalDateTime fechaHasta = hasta == null ? null : hasta.atTime(LocalTime.MAX);
        return atencionRepository.buscar(limpiar(mascota), limpiarMayusculas(especie),
                        limpiarMayusculas(estado), fechaDesde, fechaHasta)
                .stream().map(AtencionMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AtencionResponseDTO obtener(Long id) {
        return atencionRepository.buscarCompletaPorId(id).map(AtencionMapper::toResponse)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Atención no encontrada"));
    }

    @Override
    @Transactional
    public AtencionResponseDTO anular(Long id, AnularAtencionRequestDTO request) {
        Atencion atencion = atencionRepository.buscarCompletaPorId(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Atención no encontrada"));
        if (!"REGISTRADA".equals(atencion.getEstado())) {
            log.warn("Anulación rechazada para atención {}: estado {}", id, atencion.getEstado());
            throw new ApiException(HttpStatus.CONFLICT, "Solo se puede anular una atención en estado REGISTRADA");
        }
        if (LocalDateTime.now().isAfter(atencion.getFecha().plusHours(48))) {
            log.warn("Anulación rechazada para atención {}: supera las 48 horas", id);
            throw new ApiException(HttpStatus.CONFLICT, "No se puede anular una atención con más de 48 horas de registro");
        }
        atencion.setEstado("ANULADA");
        atencion.setMotivoAnulacion(request.motivo().trim());
        log.info("Atención {} anulada. Motivo: {}", atencion.getNumero(), request.motivo());
        return AtencionMapper.toResponse(atencion);
    }

    private String siguienteNumero() {
        Number siguiente = (Number) entityManager.createNativeQuery("SELECT SEQ_ATENCION_NUMERO.NEXTVAL FROM DUAL")
                .getSingleResult();
        return "AT-%06d".formatted(siguiente.longValue());
    }

    private void validarRango(LocalDate desde, LocalDate hasta) {
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "La fecha desde no puede ser posterior a la fecha hasta");
        }
    }

    private String limpiar(String value) { return value == null || value.isBlank() ? null : value.trim(); }
    private String limpiarMayusculas(String value) {
        String limpio = limpiar(value);
        return limpio == null ? null : limpio.toUpperCase();
    }
}
