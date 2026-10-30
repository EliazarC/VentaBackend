package pe.edu.upeu.vetandes.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upeu.vetandes.dto.PropietarioResponseDTO;
import pe.edu.upeu.vetandes.service.PropietarioService;
import java.util.List;

@RestController
@RequestMapping("/api/v1/propietarios")
public class PropietarioController {
    private final PropietarioService service;
    public PropietarioController(PropietarioService service) { this.service = service; }

    @GetMapping
    @PreAuthorize("hasAuthority('MASCOTA_LEER')")
    public List<PropietarioResponseDTO> listar() { return service.listar(); }
}
