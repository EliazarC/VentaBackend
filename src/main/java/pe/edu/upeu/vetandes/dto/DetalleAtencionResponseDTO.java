package pe.edu.upeu.vetandes.dto;

import java.math.BigDecimal;

public record DetalleAtencionResponseDTO(Long id, Long prestacionId, String codigoPrestacion,
                                         String nombrePrestacion, Integer cantidad,
                                         BigDecimal precioUnitario, BigDecimal subtotal) { }
