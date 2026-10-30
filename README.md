# VetAndes Backend

Backend REST del caso **VetAndes** (Examen Parcial II, versión B). Este ZIP contiene solamente el backend para abrirlo y ejecutarlo desde IntelliJ IDEA. Implementa las preguntas 1, 2 y 3 del PDF; no incluye Angular.

## Tecnologías

- Java 21
- Spring Boot 4.1.2
- Spring Web MVC, Spring Data JPA, Bean Validation y Spring Security
- Oracle Database + Flyway
- JWT Bearer (JJWT) y contraseñas BCrypt
- OpenAPI / Swagger UI

## 1. Preparar Oracle

El proyecto usa por defecto `FREEPDB1`, usuario `VETANDES` y contraseña `VetAndes2026!`. Ejecuta lo siguiente conectado como SYS en el PDB correcto (ajusta el tablespace si tu instalación utiliza otro):

```sql
CREATE USER VETANDES IDENTIFIED BY "VetAndes2026!";
GRANT CREATE SESSION, CREATE TABLE, CREATE SEQUENCE, CREATE VIEW,
      CREATE PROCEDURE, CREATE TRIGGER TO VETANDES;
ALTER USER VETANDES QUOTA UNLIMITED ON USERS;
```

Si el usuario ya existe, no vuelvas a crearlo: revisa que tenga los permisos y cuota anteriores.

La conexión predeterminada está en `src/main/resources/application.yml`:

```yaml
url: jdbc:oracle:thin:@//localhost:1522/FREEPDB1
username: VETANDES
password: VetAndes2026!
```

Cambia `ORACLE_URL`, `ORACLE_USER` y `ORACLE_PASSWORD` como variables de entorno cuando tu servicio, puerto o credenciales sean diferentes. Para Oracle XE, por ejemplo, el servicio puede ser `XEPDB1`.

## 2. Abrir y ejecutar en IntelliJ IDEA

1. Descomprime `VetAndes-Backend.zip`.
2. En IntelliJ selecciona **File > Open** y abre la carpeta `vetandes-backend` (la que contiene `pom.xml`).
3. Configura el Project SDK en **Java 21** y acepta la importación del proyecto Maven.
4. Espera que IntelliJ descargue las dependencias Maven.
5. Verifica que Oracle esté iniciado y que el usuario `VETANDES` pueda conectarse al servicio configurado.
6. Ejecuta `VetAndesApplication` desde el botón Run.

También puedes ejecutar desde una terminal que tenga Maven instalado:

```bash
mvn spring-boot:run
```

Al primer arranque Flyway ejecuta automáticamente `V1__esquema_y_semilla.sql`. Hibernate trabaja con `ddl-auto: validate`, por lo que valida el esquema y no crea ni modifica las tablas por su cuenta. No ejecutes manualmente la migración si Flyway ya la aplicó.

## 3. URLs

- API: `http://localhost:8080/api/v1`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

En Swagger pulsa **Authorize** e ingresa el token JWT sin escribir la palabra `Bearer` (Swagger la agrega mediante el esquema HTTP Bearer).

## 4. Usuarios de prueba

Los cuatro usuarios del PDF se insertan en la migración. La contraseña de prueba es `Vet2026*` para todos. Las contraseñas se guardan como BCrypt: en la migración se insertan temporalmente como `NULL` y `PasswordSeedRunner` las reemplaza por su hash al iniciar la aplicación; no se conserva texto plano en la base de datos.

| Usuario | Contraseña | Rol | Estado |
|---|---|---|---|
| `admin` | `Vet2026*` | ADMIN | Activo |
| `recepcion` | `Vet2026*` | RECEPCIONISTA | Activo |
| `veterinario` | `Vet2026*` | VETERINARIO | Activo |
| `inactivo` | `Vet2026*` | RECEPCIONISTA | Inactivo |

## 5. Obtener token

`POST /api/v1/auth/login`

```json
{
  "username": "admin",
  "password": "Vet2026*"
}
```

La respuesta contiene `token`, `tipo: Bearer`, `expiraEnSegundos: 3600`, `nombreCompleto`, `roles` y `permisos`. Para las siguientes peticiones envía:

```http
Authorization: Bearer <token>
```

`GET /api/v1/auth/me` devuelve los datos del usuario autenticado sin contraseña.

## 6. Rutas implementadas

| Verbo | Ruta | Permiso |
|---|---|---|
| POST | `/api/v1/auth/login` | Público |
| GET | `/api/v1/auth/me` | Autenticado |
| GET | `/api/v1/propietarios` | `MASCOTA_LEER` |
| GET, POST | `/api/v1/prestaciones` | `PRESTACION_LEER`, `PRESTACION_ESCRIBIR` |
| GET, PUT, DELETE | `/api/v1/prestaciones/{id}` | Lectura / escritura según verbo |
| PATCH | `/api/v1/prestaciones/{id}/estado` | `PRESTACION_ESCRIBIR` |
| GET, POST | `/api/v1/mascotas` | `MASCOTA_LEER`, `MASCOTA_ESCRIBIR` |
| GET, PUT, DELETE | `/api/v1/mascotas/{id}` | Lectura / escritura según verbo |
| POST | `/api/v1/atenciones` | `ATENCION_REGISTRAR` |
| GET | `/api/v1/atenciones?mascota&especie&estado&desde&hasta` | `ATENCION_LEER` |
| GET | `/api/v1/atenciones/{id}` | `ATENCION_LEER` |
| PATCH | `/api/v1/atenciones/{id}/anular` | `ATENCION_ANULAR` |
| GET | `/api/v1/reportes/atenciones-por-especie?desde&hasta` | `REPORTE_VER` |

Fechas de filtros: formato `YYYY-MM-DD`. Los filtros de atenciones son opcionales y combinables.

### Matriz de permisos aplicada

| Permiso | ADMIN | RECEPCIONISTA | VETERINARIO |
|---|---:|---:|---:|
| `PRESTACION_LEER` | Sí | Sí | Sí |
| `PRESTACION_ESCRIBIR` | Sí | No | Sí |
| `MASCOTA_LEER` | Sí | Sí | Sí |
| `MASCOTA_ESCRIBIR` | Sí | Sí | No |
| `ATENCION_LEER` | Sí | Sí | Sí |
| `ATENCION_REGISTRAR` | Sí | Sí | No |
| `ATENCION_ANULAR` | Sí | No | Sí |
| `REPORTE_VER` | Sí | No | Sí |

El archivo `requests.http` contiene ejemplos para login, operaciones, un caso 409, un caso 401 y comprobaciones 403 para los usuarios de recepción y veterinaria.

## 7. Reglas relevantes incluidas

- CRUD de prestaciones y mascotas con request/response DTO, relación `Mascota -> Propietario` `@ManyToOne(fetch = LAZY)`, validaciones y errores 400/404/409.
- Una prestación usada no se borra físicamente; se da de baja mediante `PATCH /prestaciones/{id}/estado` con `{"activo": false}`.
- Una mascota debe pertenecer a un propietario existente y no puede repetir nombre para el mismo propietario; tampoco se elimina si tiene atenciones.
- El registro de atención usa `@Transactional`: cabecera y detalles se guardan juntos o se revierten juntos. Precio unitario sale de Oracle. La petición no acepta precio, subtotal, descuento ni total como fuente de cálculo.
- De tres a ocho prestaciones distintas se aplica 10 % de descuento sobre el subtotal. El total lo calcula el servicio.
- Las prestaciones inactivas o no aplicables a la especie generan 409. Los detalles repetidos, cantidades fuera de 1–3 o peso inválido generan 400.
- Solo se anula una atención `REGISTRADA`, dentro de las 48 horas y con motivo de 10–200 caracteres.
- El reporte usa una consulta JPQL agregada (`GROUP BY`) y excluye las atenciones anuladas.
- Las autoridades se derivan de la matriz permiso/rol del PDF y se verifican con `@PreAuthorize` en el backend. La API es STATELESS; los tokens se verifican por firma y vencimiento en cada petición.
- CORS permite `http://localhost:4200`, métodos GET/POST/PUT/PATCH/DELETE/OPTIONS y el encabezado Authorization.

## 8. Ejemplo de registro de atención

`POST /api/v1/atenciones` con un token de `recepcion`:

```json
{
  "mascotaId": 1,
  "peso": 18.50,
  "diagnostico": "Control general y vacunación",
  "detalles": [
    { "prestacionId": 1, "cantidad": 1 },
    { "prestacionId": 2, "cantidad": 1 },
    { "prestacionId": 4, "cantidad": 1 }
  ]
}
```

Con los datos semilla, el backend calcula subtotal 165.00, descuento 16.50 y total 148.50. El número se genera correlativamente con formato `AT-000005` para la siguiente atención nueva.

## 9. Formato de error

Los errores usan el formato del examen. Los errores de validación agregan `campos`:

```json
{
  "timestamp": "2026-10-30T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "mensaje": "Datos inválidos",
  "ruta": "/api/v1/mascotas",
  "campos": {
    "fechaNacimiento": "La fecha de nacimiento no puede ser futura"
  }
}
```

## 10. Configuración para otros entornos

Variables opcionales:

| Variable | Valor por defecto |
|---|---|
| `SERVER_PORT` | `8080` |
| `ORACLE_URL` | `jdbc:oracle:thin:@//localhost:1522/FREEPDB1` |
| `ORACLE_USER` | `VETANDES` |
| `ORACLE_PASSWORD` | `VetAndes2026!` |
| `JWT_SECRET` | clave Base64 de desarrollo incluida en `application.yml` |
| `JWT_EXPIRATION_SECONDS` | `3600` |
| `CORS_ALLOWED_ORIGIN` | `http://localhost:4200` |

La clave JWT incluida es solo para desarrollo académico local; define una clave Base64 propia mediante `JWT_SECRET` antes de usar el servicio en otro entorno.
