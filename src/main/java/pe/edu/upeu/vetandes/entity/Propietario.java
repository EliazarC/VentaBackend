package pe.edu.upeu.vetandes.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "propietario")
public class Propietario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 8)
    private String dni;
    @Column(nullable = false, length = 120)
    private String nombres;
    @Column(length = 15)
    private String telefono;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
}
