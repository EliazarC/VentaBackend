package pe.edu.upeu.vetandes.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.vetandes.dto.MascotaRequestDTO;
import pe.edu.upeu.vetandes.dto.MascotaResponseDTO;
import pe.edu.upeu.vetandes.entity.Mascota;
import pe.edu.upeu.vetandes.entity.Propietario;
import pe.edu.upeu.vetandes.exception.ApiException;
import pe.edu.upeu.vetandes.mapper.MascotaMapper;
import pe.edu.upeu.vetandes.repository.AtencionRepository;
import pe.edu.upeu.vetandes.repository.MascotaRepository;
import pe.edu.upeu.vetandes.repository.PropietarioRepository;
import java.util.List;

@Service
public class MascotaServiceImpl implements pe.edu.upeu.vetandes.service.MascotaService {
    private static final Logger log = LoggerFactory.getLogger(MascotaServiceImpl.class);
    private final MascotaRepository repository;
    private final PropietarioRepository propietarioRepository;
    private final AtencionRepository atencionRepository;

    public MascotaServiceImpl(MascotaRepository repository, PropietarioRepository propietarioRepository,
                              AtencionRepository atencionRepository) {
        this.repository = repository;
        this.propietarioRepository = propietarioRepository;
        this.atencionRepository = atencionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MascotaResponseDTO> listar(Long propietarioId) {
        if (propietarioId != null) buscarPropietario(propietarioId);
        List<Mascota> items = propietarioId == null ? repository.findAllByOrderByNombreAsc()
                : repository.findByPropietario_IdOrderByNombreAsc(propietarioId);
        return items.stream().map(MascotaMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MascotaResponseDTO obtener(Long id) { return MascotaMapper.toResponse(buscar(id)); }

    @Override
    @Transactional
    public MascotaResponseDTO crear(MascotaRequestDTO request) {
        Propietario propietario = buscarPropietario(request.propietarioId());
        validarNombreDuplicado(request.nombre(), propietario.getId(), null);
        Mascota mascota = new Mascota();
        MascotaMapper.update(mascota, request, propietario);
        Mascota saved = repository.save(mascota);
        log.info("Mascota creada: {} ({})", saved.getNombre(), saved.getId());
        return MascotaMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void actualizar(Long id, MascotaRequestDTO request) {
        Mascota mascota = buscar(id);
        Propietario propietario = buscarPropietario(request.propietarioId());
        validarNombreDuplicado(request.nombre(), propietario.getId(), id);
        MascotaMapper.update(mascota, request, propietario);
        log.info("Mascota actualizada: {} ({})", mascota.getNombre(), id);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Mascota mascota = buscar(id);
        if (atencionRepository.existsByMascota_Id(id)) {
            log.warn("Eliminación de mascota {} rechazada: tiene atenciones", id);
            throw new ApiException(HttpStatus.CONFLICT, "No se puede eliminar una mascota que tiene atenciones registradas");
        }
        repository.delete(mascota);
        log.info("Mascota eliminada: {} ({})", mascota.getNombre(), id);
    }

    private Mascota buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Mascota no encontrada"));
    }

    private Propietario buscarPropietario(Long id) {
        return propietarioRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Propietario no encontrado"));
    }

    private void validarNombreDuplicado(String nombre, Long propietarioId, Long idActual) {
        boolean duplicado = idActual == null
                ? repository.existsByNombreIgnoreCaseAndPropietario_Id(nombre.trim(), propietarioId)
                : repository.existsByNombreIgnoreCaseAndPropietario_IdAndIdNot(nombre.trim(), propietarioId, idActual);
        if (duplicado) {
            log.warn("Registro de mascota rechazado: nombre {} repetido para propietario {}", nombre, propietarioId);
            throw new ApiException(HttpStatus.CONFLICT, "El propietario ya tiene una mascota con ese nombre");
        }
    }
}
