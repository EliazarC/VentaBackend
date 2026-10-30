package pe.edu.upeu.vetandes.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.vetandes.dto.EstadoPrestacionRequestDTO;
import pe.edu.upeu.vetandes.dto.PrestacionRequestDTO;
import pe.edu.upeu.vetandes.dto.PrestacionResponseDTO;
import pe.edu.upeu.vetandes.entity.Prestacion;
import pe.edu.upeu.vetandes.exception.ApiException;
import pe.edu.upeu.vetandes.mapper.PrestacionMapper;
import pe.edu.upeu.vetandes.repository.DetalleAtencionRepository;
import pe.edu.upeu.vetandes.repository.PrestacionRepository;
import java.util.List;

@Service
public class PrestacionServiceImpl implements pe.edu.upeu.vetandes.service.PrestacionService {
    private static final Logger log = LoggerFactory.getLogger(PrestacionServiceImpl.class);
    private final PrestacionRepository repository;
    private final DetalleAtencionRepository detalleRepository;

    public PrestacionServiceImpl(PrestacionRepository repository, DetalleAtencionRepository detalleRepository) {
        this.repository = repository;
        this.detalleRepository = detalleRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrestacionResponseDTO> listar(String especie) {
        List<Prestacion> items = (especie == null || especie.isBlank())
                ? repository.findAllByOrderByCodigoAsc()
                : repository.findByEspecieAplicableOrEspecieAplicableOrderByCodigoAsc(especie.trim().toUpperCase(), "TODAS");
        return items.stream().map(PrestacionMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PrestacionResponseDTO obtener(Long id) { return PrestacionMapper.toResponse(buscar(id)); }

    @Override
    @Transactional
    public PrestacionResponseDTO crear(PrestacionRequestDTO request) {
        if (repository.existsByCodigoIgnoreCase(request.codigo().trim())) {
            log.warn("Alta de prestación rechazada: código duplicado {}", request.codigo());
            throw new ApiException(HttpStatus.CONFLICT, "Ya existe una prestación con ese código");
        }
        Prestacion entity = new Prestacion();
        PrestacionMapper.update(entity, request);
        entity.setActivo(true);
        Prestacion saved = repository.save(entity);
        log.info("Prestación creada: {} ({})", saved.getCodigo(), saved.getId());
        return PrestacionMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void actualizar(Long id, PrestacionRequestDTO request) {
        Prestacion entity = buscar(id);
        if (repository.existsByCodigoIgnoreCaseAndIdNot(request.codigo().trim(), id)) {
            log.warn("Actualización de prestación {} rechazada: código duplicado", id);
            throw new ApiException(HttpStatus.CONFLICT, "Ya existe otra prestación con ese código");
        }
        PrestacionMapper.update(entity, request);
        log.info("Prestación actualizada: {} ({})", entity.getCodigo(), id);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Prestacion entity = buscar(id);
        if (detalleRepository.existsByPrestacion_Id(id)) {
            log.warn("Baja física de prestación {} rechazada: tiene atenciones relacionadas", id);
            throw new ApiException(HttpStatus.CONFLICT,
                    "La prestación ya fue utilizada; utilice PATCH /prestaciones/{id}/estado para darla de baja");
        }
        repository.delete(entity);
        log.info("Prestación eliminada físicamente: {} ({})", entity.getCodigo(), id);
    }

    @Override
    @Transactional
    public PrestacionResponseDTO cambiarEstado(Long id, EstadoPrestacionRequestDTO request) {
        Prestacion entity = buscar(id);
        entity.setActivo(request.activo());
        log.info("Estado de prestación {} actualizado a {}", id, request.activo());
        return PrestacionMapper.toResponse(entity);
    }

    private Prestacion buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Prestación no encontrada"));
    }
}
