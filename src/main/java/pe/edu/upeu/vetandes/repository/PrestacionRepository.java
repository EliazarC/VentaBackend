package pe.edu.upeu.vetandes.repository;

import pe.edu.upeu.vetandes.entity.Prestacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PrestacionRepository extends JpaRepository<Prestacion, Long> {
    boolean existsByCodigoIgnoreCase(String codigo);
    boolean existsByCodigoIgnoreCaseAndIdNot(String codigo, Long id);
    List<Prestacion> findAllByOrderByCodigoAsc();
    List<Prestacion> findByEspecieAplicableOrEspecieAplicableOrderByCodigoAsc(String especie, String todas);
}
