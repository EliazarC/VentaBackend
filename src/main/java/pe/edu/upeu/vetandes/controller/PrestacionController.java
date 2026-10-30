package pe.edu.upeu.vetandes.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.vetandes.dto.EstadoPrestacionRequestDTO;
import pe.edu.upeu.vetandes.dto.PrestacionRequestDTO;
import pe.edu.upeu.vetandes.dto.PrestacionResponseDTO;
import pe.edu.upeu.vetandes.service.PrestacionService;
import java.util.List;

@RestController
@RequestMapping("/api/v1/prestaciones")
public class PrestacionController {
    private final PrestacionService service;
    public PrestacionController(PrestacionService service) { this.service = service; }

    @GetMapping
    @PreAuthorize("hasAuthority('PRESTACION_LEER')")
    public List<PrestacionResponseDTO> listar(@RequestParam(required = false) String especie) {
        return service.listar(especie);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PRESTACION_LEER')")
    public PrestacionResponseDTO obtener(@PathVariable Long id) { return service.obtener(id); }

    @PostMapping
    @PreAuthorize("hasAuthority('PRESTACION_ESCRIBIR')")
    public ResponseEntity<PrestacionResponseDTO> crear(@Valid @RequestBody PrestacionRequestDTO request) {
        return ResponseEntity.status(201).body(service.crear(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PRESTACION_ESCRIBIR')")
    public ResponseEntity<Void> actualizar(@PathVariable Long id, @Valid @RequestBody PrestacionRequestDTO request) {
        service.actualizar(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PRESTACION_ESCRIBIR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAuthority('PRESTACION_ESCRIBIR')")
    public PrestacionResponseDTO cambiarEstado(@PathVariable Long id,
                                                @Valid @RequestBody EstadoPrestacionRequestDTO request) {
        return service.cambiarEstado(id, request);
    }
}
