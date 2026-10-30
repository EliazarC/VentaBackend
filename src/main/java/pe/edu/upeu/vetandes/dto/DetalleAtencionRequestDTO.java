package pe.edu.upeu.vetandes.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record DetalleAtencionRequestDTO(
        @NotNull(message = "prestacionId es obligatorio") @Positive(message = "prestacionId debe ser mayor que cero") Long prestacionId,
        @NotNull(message = "La cantidad es obligatoria") @Min(value = 1, message = "La cantidad mínima es 1")
        @Max(value = 3, message = "La cantidad máxima es 3") Integer cantidad) { }
