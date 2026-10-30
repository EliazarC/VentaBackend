package pe.edu.upeu.vetandes.dto;

import java.math.BigDecimal;

public record ReporteAtencionesEspecieDTO(String especie, Long cantidadAtenciones, BigDecimal importeTotal) { }
