package pe.edu.upeu.vetandes.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.vetandes.dto.MascotaRequestDTO;
import pe.edu.upeu.vetandes.dto.MascotaResponseDTO;
import pe.edu.upeu.vetandes.service.MascotaService;
import java.util.List;

@RestController
@RequestMapping("/api/v1/mascotas")
public class MascotaController {
    private final MascotaService service;
    public MascotaController(MascotaService service) { this.service = service; }

    @GetMapping
    @PreAuthorize("hasAuthority('MASCOTA_LEER')")
    public List<MascotaResponseDTO> listar(@RequestParam(required = false) Long propietarioId) {
        return service.listar(propietarioId);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('MASCOTA_LEER')")
    public MascotaResponseDTO obtener(@PathVariable Long id) { return service.obtener(id); }

    @PostMapping
    @PreAuthorize("hasAuthority('MASCOTA_ESCRIBIR')")
    public ResponseEntity<MascotaResponseDTO> crear(@Valid @RequestBody MascotaRequestDTO request) {
        return ResponseEntity.status(201).body(service.crear(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('MASCOTA_ESCRIBIR')")
    public ResponseEntity<Void> actualizar(@PathVariable Long id, @Valid @RequestBody MascotaRequestDTO request) {
        service.actualizar(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MASCOTA_ESCRIBIR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
