package pe.edu.upeu.vetandes.dto;

import java.time.LocalDate;

public record MascotaResponseDTO(Long id, String nombre, String especie, String raza,
                                 LocalDate fechaNacimiento, String sexo,
                                 Long propietarioId, String propietarioNombre) { }
