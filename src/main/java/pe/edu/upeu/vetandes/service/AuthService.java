package pe.edu.upeu.vetandes.service;

import pe.edu.upeu.vetandes.dto.LoginRequestDTO;
import pe.edu.upeu.vetandes.dto.LoginResponseDTO;
import pe.edu.upeu.vetandes.dto.UsuarioActualDTO;

public interface AuthService {
    LoginResponseDTO login(LoginRequestDTO request);
    UsuarioActualDTO actual(String username);
}
