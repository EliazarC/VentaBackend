package pe.edu.upeu.vetandes.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pe.edu.upeu.vetandes.repository.UsuarioRepository;
import java.util.ArrayList;
import java.util.List;

@Service
public class UsuarioDetailsService implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var usuario = usuarioRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        List<String> roles = usuario.getRoles().stream().map(r -> r.getNombre()).toList();
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        PermisosPorRol.permisosDe(roles).forEach(p -> authorities.add(new SimpleGrantedAuthority(p)));
        roles.forEach(r -> authorities.add(new SimpleGrantedAuthority("ROLE_" + r)));
        return User.withUsername(usuario.getUsername())
                .password(usuario.getPassword() == null ? "" : usuario.getPassword())
                .authorities(authorities)
                .disabled(!Boolean.TRUE.equals(usuario.getActivo()))
                .build();
    }
}
