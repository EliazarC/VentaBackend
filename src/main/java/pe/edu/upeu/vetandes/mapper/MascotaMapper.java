package pe.edu.upeu.vetandes.mapper;

import pe.edu.upeu.vetandes.dto.MascotaRequestDTO;
import pe.edu.upeu.vetandes.dto.MascotaResponseDTO;
import pe.edu.upeu.vetandes.entity.Mascota;
import pe.edu.upeu.vetandes.entity.Propietario;

public final class MascotaMapper {
    private MascotaMapper() { }

    public static MascotaResponseDTO toResponse(Mascota entity) {
        return new MascotaResponseDTO(entity.getId(), entity.getNombre(), entity.getEspecie(),
                entity.getRaza(), entity.getFechaNacimiento(), entity.getSexo(),
                entity.getPropietario().getId(), entity.getPropietario().getNombres());
    }

    public static void update(Mascota entity, MascotaRequestDTO request, Propietario propietario) {
        entity.setNombre(request.nombre().trim());
        entity.setEspecie(request.especie().trim().toUpperCase());
        entity.setRaza(request.raza() == null || request.raza().isBlank() ? null : request.raza().trim());
        entity.setFechaNacimiento(request.fechaNacimiento());
        entity.setSexo(request.sexo().trim().toUpperCase());
        entity.setPropietario(propietario);
    }
}
