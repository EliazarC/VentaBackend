package pe.edu.upeu.vetandes.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "prestacion")
public class Prestacion {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 7)
    private String codigo;
    @Column(nullable = false, length = 80)
    private String nombre;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;
    @Column(name = "especie_aplicable", nullable = false, length = 6)
    private String especieAplicable;
    @Column(name = "activo", nullable = false, precision = 1, scale = 0)
    private Integer activo = 1;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    public String getEspecieAplicable() { return especieAplicable; }
    public void setEspecieAplicable(String especieAplicable) { this.especieAplicable = especieAplicable; }
    public Boolean getActivo() { return Integer.valueOf(1).equals(activo); }
    public void setActivo(Boolean activo) { this.activo = Boolean.TRUE.equals(activo) ? 1 : 0; }
}
