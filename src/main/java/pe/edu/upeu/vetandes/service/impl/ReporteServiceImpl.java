package pe.edu.upeu.vetandes.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.vetandes.dto.ReporteAtencionesEspecieDTO;
import pe.edu.upeu.vetandes.exception.ApiException;
import pe.edu.upeu.vetandes.repository.AtencionRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class ReporteServiceImpl implements pe.edu.upeu.vetandes.service.ReporteService {
    private final AtencionRepository repository;
    public ReporteServiceImpl(AtencionRepository repository) { this.repository = repository; }

    @Override
    @Transactional(readOnly = true)
    public List<ReporteAtencionesEspecieDTO> porEspecie(LocalDate desde, LocalDate hasta) {
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "La fecha desde no puede ser posterior a la fecha hasta");
        }
        LocalDateTime inicio = desde == null ? null : desde.atStartOfDay();
        LocalDateTime fin = hasta == null ? null : hasta.atTime(LocalTime.MAX);
        return repository.reportePorEspecie(inicio, fin).stream()
                .map(p -> new ReporteAtencionesEspecieDTO(p.getEspecie(), p.getCantidadAtenciones(), p.getImporteTotal()))
                .toList();
    }
}
