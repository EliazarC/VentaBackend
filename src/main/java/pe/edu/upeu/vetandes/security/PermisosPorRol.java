package pe.edu.upeu.vetandes.security;

import java.util.*;

public final class PermisosPorRol {
    private static final Map<String, Set<Permiso>> MATRIZ;
    static {
        Map<String, Set<Permiso>> matriz = new HashMap<>();
        matriz.put("ADMIN", EnumSet.allOf(Permiso.class));
        matriz.put("RECEPCIONISTA", EnumSet.of(
                Permiso.PRESTACION_LEER,
                Permiso.MASCOTA_LEER, Permiso.MASCOTA_ESCRIBIR,
                Permiso.ATENCION_LEER, Permiso.ATENCION_REGISTRAR));
        matriz.put("VETERINARIO", EnumSet.of(
                Permiso.PRESTACION_LEER, Permiso.PRESTACION_ESCRIBIR,
                Permiso.MASCOTA_LEER, Permiso.ATENCION_LEER,
                Permiso.ATENCION_ANULAR, Permiso.REPORTE_VER));
        MATRIZ = Collections.unmodifiableMap(matriz);
    }

    private PermisosPorRol() { }

    public static Set<String> permisosDe(Collection<String> nombresRoles) {
        Set<Permiso> union = EnumSet.noneOf(Permiso.class);
        for (String rol : nombresRoles) union.addAll(MATRIZ.getOrDefault(rol.toUpperCase(Locale.ROOT), Set.of()));
        Set<String> resultado = new TreeSet<>();
        union.forEach(p -> resultado.add(p.name()));
        return Collections.unmodifiableSet(resultado);
    }
}
