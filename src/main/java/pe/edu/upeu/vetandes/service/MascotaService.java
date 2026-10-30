package pe.edu.upeu.vetandes.service;

import pe.edu.upeu.vetandes.dto.MascotaRequestDTO;
import pe.edu.upeu.vetandes.dto.MascotaResponseDTO;
import java.util.List;

public interface MascotaService {
    List<MascotaResponseDTO> listar(Long propietarioId);
    MascotaResponseDTO obtener(Long id);
    MascotaResponseDTO crear(MascotaRequestDTO request);
    void actualizar(Long id, MascotaRequestDTO request);
    void eliminar(Long id);
}
