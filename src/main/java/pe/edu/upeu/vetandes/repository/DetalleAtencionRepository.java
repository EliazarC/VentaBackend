package pe.edu.upeu.vetandes.repository;

import pe.edu.upeu.vetandes.entity.DetalleAtencion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleAtencionRepository extends JpaRepository<DetalleAtencion, Long> {
    boolean existsByPrestacion_Id(Long prestacionId);
}
