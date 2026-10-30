package pe.edu.upeu.vetandes.dto;

import java.math.BigDecimal;

public record PrestacionResponseDTO(Long id, String codigo, String nombre, BigDecimal precio,
                                    String especieAplicable, Boolean activo) { }
