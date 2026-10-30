package pe.edu.upeu.vetandes.mapper;

import pe.edu.upeu.vetandes.dto.AtencionResponseDTO;
import pe.edu.upeu.vetandes.dto.DetalleAtencionResponseDTO;
import pe.edu.upeu.vetandes.entity.Atencion;
import java.util.stream.Collectors;

public final class AtencionMapper {
    private AtencionMapper() { }

    public static AtencionResponseDTO toResponse(Atencion entity) {
        var mascota = entity.getMascota();
        var detalles = entity.getDetalles().stream().map(d -> new DetalleAtencionResponseDTO(
                d.getId(), d.getPrestacion().getId(), d.getPrestacion().getCodigo(),
                d.getPrestacion().getNombre(), d.getCantidad(), d.getPrecioUnitario(), d.getSubtotal()))
                .collect(Collectors.toList());
        return new AtencionResponseDTO(entity.getId(), entity.getNumero(), entity.getFecha(),
                mascota.getId(), mascota.getNombre(), mascota.getEspecie(),
                mascota.getPropietario().getNombres(), entity.getPeso(), entity.getDiagnostico(),
                entity.getEstado(), entity.getSubtotal(), entity.getDescuento(), entity.getTotal(),
                entity.getUsuarioRegistro(), entity.getMotivoAnulacion(), detalles);
    }
}
