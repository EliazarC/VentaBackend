package pe.edu.upeu.vetandes.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.vetandes.config.JwtService;
import pe.edu.upeu.vetandes.dto.LoginRequestDTO;
import pe.edu.upeu.vetandes.dto.LoginResponseDTO;
import pe.edu.upeu.vetandes.dto.UsuarioActualDTO;
import pe.edu.upeu.vetandes.entity.Usuario;
import pe.edu.upeu.vetandes.exception.ApiException;
import pe.edu.upeu.vetandes.repository.UsuarioRepository;
import pe.edu.upeu.vetandes.security.PermisosPorRol;
import java.util.Set;
import java.util.TreeSet;

@Service
public class AuthServiceImpl implements pe.edu.upeu.vetandes.service.AuthService {
    private static final String MENSAJE_LOGIN = "Usuario o contraseña incorrectos";
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final String dummyHash;

    public AuthServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.dummyHash = passwordEncoder.encode("vetandes-user-not-found-dummy-password");
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginRequestDTO request) {
        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(request.username().trim()).orElse(null);
        String hash = usuario == null || usuario.getPassword() == null ? dummyHash : usuario.getPassword();
        boolean passwordCorrecta = passwordEncoder.matches(request.password(), hash);
        if (usuario == null || !passwordCorrecta || !Boolean.TRUE.equals(usuario.getActivo())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, MENSAJE_LOGIN);
        }
        Set<String> roles = new TreeSet<>();
        usuario.getRoles().forEach(rol -> roles.add(rol.getNombre().toUpperCase()));
        Set<String> permisos = PermisosPorRol.permisosDe(roles);
        String token = jwtService.generar(usuario.getUsername(), roles, permisos);
        return new LoginResponseDTO(token, "Bearer", jwtService.getExpirationSeconds(),
                usuario.getNombreCompleto(), roles, permisos);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioActualDTO actual(String username) {
        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Usuario no disponible"));
        Set<String> roles = new TreeSet<>();
        usuario.getRoles().forEach(rol -> roles.add(rol.getNombre().toUpperCase()));
        return new UsuarioActualDTO(usuario.getUsername(), usuario.getNombreCompleto(),
                roles, PermisosPorRol.permisosDe(roles));
    }
}
