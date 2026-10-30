package pe.edu.upeu.vetandes.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.vetandes.dto.AnularAtencionRequestDTO;
import pe.edu.upeu.vetandes.dto.AtencionRequestDTO;
import pe.edu.upeu.vetandes.dto.AtencionResponseDTO;
import pe.edu.upeu.vetandes.service.AtencionService;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/atenciones")
public class AtencionController {
    private final AtencionService service;
    public AtencionController(AtencionService service) { this.service = service; }

    @PostMapping
    @PreAuthorize("hasAuthority('ATENCION_REGISTRAR')")
    public ResponseEntity<AtencionResponseDTO> registrar(@Valid @RequestBody AtencionRequestDTO request,
                                                          Authentication authentication) {
        return ResponseEntity.status(201).body(service.registrar(request, authentication.getName()));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ATENCION_LEER')")
    public List<AtencionResponseDTO> consultar(
            @RequestParam(required = false) String mascota,
            @RequestParam(required = false) String especie,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) LocalDate desde,
            @RequestParam(required = false) LocalDate hasta) {
        return service.consultar(mascota, especie, estado, desde, hasta);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ATENCION_LEER')")
    public AtencionResponseDTO obtener(@PathVariable Long id) { return service.obtener(id); }

    @PatchMapping("/{id}/anular")
    @PreAuthorize("hasAuthority('ATENCION_ANULAR')")
    public AtencionResponseDTO anular(@PathVariable Long id, @Valid @RequestBody AnularAtencionRequestDTO request) {
        return service.anular(id, request);
    }
}
