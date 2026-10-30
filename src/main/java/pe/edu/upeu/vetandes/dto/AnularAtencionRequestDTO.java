package pe.edu.upeu.vetandes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AnularAtencionRequestDTO(
        @NotBlank(message = "El motivo de anulación es obligatorio")
        @Size(min = 10, max = 200, message = "El motivo debe tener entre 10 y 200 caracteres")
        String motivo) { }
