package pe.edu.upeu.vetandes.service;

import pe.edu.upeu.vetandes.dto.AnularAtencionRequestDTO;
import pe.edu.upeu.vetandes.dto.AtencionRequestDTO;
import pe.edu.upeu.vetandes.dto.AtencionResponseDTO;
import java.time.LocalDate;
import java.util.List;

public interface AtencionService {
    AtencionResponseDTO registrar(AtencionRequestDTO request, String usuarioRegistro);
    List<AtencionResponseDTO> consultar(String mascota, String especie, String estado, LocalDate desde, LocalDate hasta);
    AtencionResponseDTO obtener(Long id);
    AtencionResponseDTO anular(Long id, AnularAtencionRequestDTO request);
}
