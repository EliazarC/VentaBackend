package pe.edu.upeu.vetandes.service;

import pe.edu.upeu.vetandes.dto.EstadoPrestacionRequestDTO;
import pe.edu.upeu.vetandes.dto.PrestacionRequestDTO;
import pe.edu.upeu.vetandes.dto.PrestacionResponseDTO;
import java.util.List;

public interface PrestacionService {
    List<PrestacionResponseDTO> listar(String especie);
    PrestacionResponseDTO obtener(Long id);
    PrestacionResponseDTO crear(PrestacionRequestDTO request);
    void actualizar(Long id, PrestacionRequestDTO request);
    void eliminar(Long id);
    PrestacionResponseDTO cambiarEstado(Long id, EstadoPrestacionRequestDTO request);
}
