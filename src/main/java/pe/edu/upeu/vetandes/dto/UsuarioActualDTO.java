package pe.edu.upeu.vetandes.dto;

import java.util.Set;

public record UsuarioActualDTO(String username, String nombreCompleto,
                               Set<String> roles, Set<String> permisos) { }
