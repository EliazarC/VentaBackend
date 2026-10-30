package pe.edu.upeu.vetandes.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.vetandes.dto.PropietarioResponseDTO;
import pe.edu.upeu.vetandes.repository.PropietarioRepository;
import java.util.List;

@Service
public class PropietarioServiceImpl implements pe.edu.upeu.vetandes.service.PropietarioService {
    private final PropietarioRepository repository;
    public PropietarioServiceImpl(PropietarioRepository repository) { this.repository = repository; }

    @Override
    @Transactional(readOnly = true)
    public List<PropietarioResponseDTO> listar() {
        return repository.findAllByOrderByNombresAsc().stream()
                .map(p -> new PropietarioResponseDTO(p.getId(), p.getDni(), p.getNombres(), p.getTelefono()))
                .toList();
    }
}
