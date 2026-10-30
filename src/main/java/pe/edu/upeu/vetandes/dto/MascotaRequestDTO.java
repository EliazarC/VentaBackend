package pe.edu.upeu.vetandes.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record MascotaRequestDTO(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 2, max = 40, message = "El nombre debe tener entre 2 y 40 caracteres")
        String nombre,
        @NotBlank(message = "La especie es obligatoria")
        @Pattern(regexp = "PERRO|GATO|OTRO", message = "La especie debe ser PERRO, GATO u OTRO")
        String especie,
        @Size(max = 40, message = "La raza admite hasta 40 caracteres")
        String raza,
        @NotNull(message = "La fecha de nacimiento es obligatoria")
        @PastOrPresent(message = "La fecha de nacimiento no puede ser futura")
        LocalDate fechaNacimiento,
        @NotBlank(message = "El sexo es obligatorio")
        @Pattern(regexp = "M|H", message = "El sexo debe ser M o H")
        String sexo,
        @NotNull(message = "El propietarioId es obligatorio")
        @Positive(message = "El propietarioId debe ser mayor que cero")
        Long propietarioId) { }
