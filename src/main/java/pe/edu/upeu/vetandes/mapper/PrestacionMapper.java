package pe.edu.upeu.vetandes.mapper;

import pe.edu.upeu.vetandes.dto.PrestacionRequestDTO;
import pe.edu.upeu.vetandes.dto.PrestacionResponseDTO;
import pe.edu.upeu.vetandes.entity.Prestacion;

public final class PrestacionMapper {
    private PrestacionMapper() { }

    public static PrestacionResponseDTO toResponse(Prestacion entity) {
        return new PrestacionResponseDTO(entity.getId(), entity.getCodigo(), entity.getNombre(),
                entity.getPrecio(), entity.getEspecieAplicable(), entity.getActivo());
    }

    public static void update(Prestacion entity, PrestacionRequestDTO request) {
        entity.setCodigo(request.codigo().trim().toUpperCase());
        entity.setNombre(request.nombre().trim());
        entity.setPrecio(request.precio());
        entity.setEspecieAplicable(request.especieAplicable().trim().toUpperCase());
    }
}
