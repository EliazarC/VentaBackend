package pe.edu.upeu.vetandes.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.vetandes.dto.ReporteAtencionesEspecieDTO;
import pe.edu.upeu.vetandes.service.ReporteService;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reportes")
public class ReporteController {
    private final ReporteService service;
    public ReporteController(ReporteService service) { this.service = service; }

    @GetMapping("/atenciones-por-especie")
    @PreAuthorize("hasAuthority('REPORTE_VER')")
    public List<ReporteAtencionesEspecieDTO> porEspecie(
            @RequestParam(required = false) LocalDate desde,
            @RequestParam(required = false) LocalDate hasta) {
        return service.porEspecie(desde, hasta);
    }
}
