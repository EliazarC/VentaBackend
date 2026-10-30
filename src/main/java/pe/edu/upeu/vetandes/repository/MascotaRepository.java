package pe.edu.upeu.vetandes.repository;

import pe.edu.upeu.vetandes.entity.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MascotaRepository extends JpaRepository<Mascota, Long> {
    boolean existsByNombreIgnoreCaseAndPropietario_Id(String nombre, Long propietarioId);
    boolean existsByNombreIgnoreCaseAndPropietario_IdAndIdNot(String nombre, Long propietarioId, Long id);
    List<Mascota> findAllByOrderByNombreAsc();
    List<Mascota> findByPropietario_IdOrderByNombreAsc(Long propietarioId);
}
