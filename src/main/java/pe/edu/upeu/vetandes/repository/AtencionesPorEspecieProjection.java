package pe.edu.upeu.vetandes.repository;

import java.math.BigDecimal;

public interface AtencionesPorEspecieProjection {
    String getEspecie();
    Long getCantidadAtenciones();
    BigDecimal getImporteTotal();
}
