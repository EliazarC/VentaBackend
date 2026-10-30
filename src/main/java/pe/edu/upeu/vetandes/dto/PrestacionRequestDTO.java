package pe.edu.upeu.vetandes.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record PrestacionRequestDTO(
        @NotBlank(message = "El código es obligatorio")
        @Pattern(regexp = "PRS-[0-9]{3}", message = "El código debe tener el formato PRS-000")
        String codigo,
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 3, max = 80, message = "El nombre debe tener entre 3 y 80 caracteres")
        String nombre,
        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor que cero")
        @Digits(integer = 8, fraction = 2, message = "El precio admite hasta 2 decimales")
        BigDecimal precio,
        @NotBlank(message = "La especie aplicable es obligatoria")
        @Pattern(regexp = "PERRO|GATO|TODAS", message = "La especie debe ser PERRO, GATO o TODAS")
        String especieAplicable) { }
