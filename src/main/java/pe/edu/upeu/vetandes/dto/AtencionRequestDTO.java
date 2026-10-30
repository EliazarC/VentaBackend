package pe.edu.upeu.vetandes.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

public record AtencionRequestDTO(
        @NotNull(message = "mascotaId es obligatorio") @Positive(message = "mascotaId debe ser mayor que cero") Long mascotaId,
        @NotNull(message = "El peso es obligatorio")
        @DecimalMin(value = "0.0", inclusive = false, message = "El peso debe ser mayor que cero")
        @Digits(integer = 3, fraction = 2, message = "El peso admite hasta 2 decimales") BigDecimal peso,
        @Size(max = 300, message = "El diagnóstico admite hasta 300 caracteres") String diagnostico,
        @NotEmpty(message = "La atención debe incluir al menos un detalle")
        @Size(min = 1, max = 8, message = "La atención debe tener entre 1 y 8 detalles")
        List<@Valid DetalleAtencionRequestDTO> detalles) { }
