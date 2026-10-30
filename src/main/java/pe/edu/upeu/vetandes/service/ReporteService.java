package pe.edu.upeu.vetandes.service;

import pe.edu.upeu.vetandes.dto.ReporteAtencionesEspecieDTO;
import java.time.LocalDate;
import java.util.List;

public interface ReporteService {
    List<ReporteAtencionesEspecieDTO> porEspecie(LocalDate desde, LocalDate hasta);
}
