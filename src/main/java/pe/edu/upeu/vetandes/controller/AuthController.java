package pe.edu.upeu.vetandes.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.vetandes.dto.LoginRequestDTO;
import pe.edu.upeu.vetandes.dto.LoginResponseDTO;
import pe.edu.upeu.vetandes.dto.UsuarioActualDTO;
import pe.edu.upeu.vetandes.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService service;
    public AuthController(AuthService service) { this.service = service; }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(service.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioActualDTO> me(Authentication authentication) {
        return ResponseEntity.ok(service.actual(authentication.getName()));
    }
}
