package pe.edu.upeu.vetandes.service.impl;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.vetandes.repository.UsuarioRepository;

@Component
public class PasswordSeedRunner implements CommandLineRunner {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordSeedRunner(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        usuarioRepository.findAll().stream()
                .filter(usuario -> usuario.getPassword() == null || usuario.getPassword().isBlank())
                .forEach(usuario -> usuario.setPassword(passwordEncoder.encode("Vet2026*")));
    }
}
