package pe.edu.upeu.vetandes.repository;

import pe.edu.upeu.vetandes.entity.Atencion;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AtencionRepository extends JpaRepository<Atencion, Long> {
    boolean existsByMascota_Id(Long mascotaId);

    @EntityGraph(attributePaths = {"mascota", "mascota.propietario", "detalles", "detalles.prestacion"})
    @Query("""
        select distinct a from Atencion a
        join a.mascota m
        where (:mascota is null or lower(m.nombre) like lower(concat('%', :mascota, '%')))
          and (:especie is null or m.especie = :especie)
          and (:estado is null or a.estado = :estado)
          and (:desde is null or a.fecha >= :desde)
          and (:hasta is null or a.fecha <= :hasta)
        order by a.fecha desc
        """)
    List<Atencion> buscar(@Param("mascota") String mascota,
                          @Param("especie") String especie,
                          @Param("estado") String estado,
                          @Param("desde") LocalDateTime desde,
                          @Param("hasta") LocalDateTime hasta);

    @EntityGraph(attributePaths = {"mascota", "mascota.propietario", "detalles", "detalles.prestacion"})
    @Query("select a from Atencion a where a.id = :id")
    Optional<Atencion> buscarCompletaPorId(@Param("id") Long id);

    @Query("""
        select m.especie as especie,
               count(a.id) as cantidadAtenciones,
               sum(a.total) as importeTotal
        from Atencion a join a.mascota m
        where a.estado <> 'ANULADA'
          and (:desde is null or a.fecha >= :desde)
          and (:hasta is null or a.fecha <= :hasta)
        group by m.especie
        order by sum(a.total) desc
        """)
    List<AtencionesPorEspecieProjection> reportePorEspecie(@Param("desde") LocalDateTime desde,
                                                            @Param("hasta") LocalDateTime hasta);
}
