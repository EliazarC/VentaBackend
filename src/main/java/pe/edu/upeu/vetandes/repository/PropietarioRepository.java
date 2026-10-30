package pe.edu.upeu.vetandes.repository;

import pe.edu.upeu.vetandes.entity.Propietario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PropietarioRepository extends JpaRepository<Propietario, Long> {
    List<Propietario> findAllByOrderByNombresAsc();
}
