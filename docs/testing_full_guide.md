## Guía Completa De Pruebas De API

### 0. Preparativos
- Levanta el stack: `docker compose up --build`.
- Importa en Postman `docs/postman/SmartParking.postman_collection.json` y define:
  - `baseUrl = http://localhost:8080`
  - `spaceStatus = available` (o el que quieras filtrar)
  - Deja vacías `authToken`, `authTokenAdmin`, `authTokenIot`, `userId`, `spaceId`, `reservationId`, `sessionId`, `tokenHash`, `notificationId`.
- Verifica que la BD esté limpia. Si necesitas resetear:  
  `docker compose down && docker volume rm smartparking-backend_postgres_data && docker compose up --build`.

> En los pasos siguientes, se indica qué variable debes completar tras cada respuesta exitosa.

---

### 1. Crear Usuarios Base (Admin + Estudiante)
1. **Registrar administrador**  
   - Request: `Auth & Users / Registrar Administrador`  
   - Guarda `adminUserId` (opcional para auditoría).
2. **Login administrador**  
   - Request: `Auth & Users / Login` (con email del admin).  
   - Copia de la respuesta: `token → authTokenAdmin`, `sessionId → sessionIdAdmin`.
3. **Crear usuario universitario (desde admin)**  
   - Request: `Auth & Users / Crear Usuario (Admin)`  
   - Guarda `userId` retornado (lo usarás en la mayoría de pruebas).
4. **Login usuario**  
   - Request: `Auth & Users / Login` con email del usuario creado.  
   - Asigna `token → authToken`, `sessionId → sessionId`.
5. **Opcional: registrar cuenta IoT**  
   - Usa el endpoint de registro admin (misma contraseña) y luego login para obtener `authTokenIot` para pruebas IoT.

---

### 2. Perfiles Y Sesiones
1. **Crear perfil de usuario**  
   - `User Profiles / Crear Perfil` con `userId`.  
   - Anota `firstName/lastName` para validar cambios.
2. **Consultar perfil**  
   - `User Profiles / Obtener Perfil` para confirmar datos.
3. **Actualizar perfil**  
   - `User Profiles / Actualizar Perfil` y valida con un nuevo `GET`.
4. **Registrar sesión manual (QA)**  
   - `User Sessions / Crear Sesión` con un `tokenHash` ficticio (p. ej. `hash-qa-123`).  
   - Copia `sessionId` devuelto → `manualSessionId`.
5. **Listar sesiones activas**  
   - `User Sessions / Sesiones Activas` (valida que aparezcan `sessionId` y `manualSessionId`).
6. **Buscar por token**  
   - `User Sessions / Buscar por Token` usando `hash-qa-123`.
7. **Invalidar la sesión manual**  
   - `User Sessions / Eliminar Sesión` con `manualSessionId`.

---

### 3. Inventario De Espacios (Admin)
1. **Crear espacio**  
   - `Parking Spaces / Crear Espacio` (requiere `authTokenAdmin`).  
   - Guarda `spaceId` y `code`.
2. **Listar espacios**  
   - `Parking Spaces / Listar Espacios` y valida que aparezca el nuevo.
3. **Filtrar por estado**  
   - `Parking Spaces / Filtrar por Estado` con `spaceStatus=available`.
4. **Actualizar espacio**  
   - Cambia el código o estado con `Parking Spaces / Actualizar Espacio`.  
   - Repite el filtro para confirmar cambio; vuelve a dejarlo en `available` antes de continuar.

---

### 4. Flujo De Reservas (Usuario)
1. **Crear reserva**  
   - `Reservations / Crear Reserva` con `spaceId` y hora futura (UTC).  
   - Guarda `reservationId`.
2. **Confirmar reserva**  
   - `Reservations / Confirmar Reserva`.
3. **Ver reserva activa**  
   - `Reservations / Reserva Activa` → revisa `status`, `minutesUntilArrival`, `canCancel`.
4. **Registrar llegada**  
   - `Reservations / Activar Reserva (Usuario)` (ajusta la hora actual dentro de la ventana).  
   - Verifica que `status` sea `active`.
5. **Completar reserva**  
   - `Reservations / Completar Reserva` (calcula costo, libera espacio).
6. **Historial**  
   - `Reservations / Historial de Reservas` → la reserva debe aparecer como `completed`.

#### Variantes/Uso adicional
- **Cancelar antes de tiempo**: repite creación/confirmación y usa `Cancelar Reserva` (con `reason`).  
  Revisa `Historial` para verificar `cancelled`.
- **Forzar expiración**: crea nueva reserva pendiente/confirmada y ejecuta `Expirar Reserva Manualmente`.  
  En historial debe figurar como `expired`.

---

### 5. Integración IoT
1. **Obtener `authTokenIot`** (si no lo tienes, crea usuario IoT y realiza login).  
2. **Activar vía IoT**  
   - Crea una reserva confirmada (no activada).  
   - Usa `IoT Integration / Activar Reserva (IoT)` con `spaceId` y `authTokenIot`.  
   - Verifica que la reserva pase a `active` y que se registre el `ArrivalEvent` (opcional consultar BD).

---

### 6. Notificaciones
1. **Preferencias por defecto**  
   - `Notifications / Obtener Preferencias` (debería devolver lista vacía).
2. **Actualizar preferencias**  
   - `Notifications / Actualizar Preferencias` con plantilla (push habilitado, quiet hours).  
   - Confirma con el `GET` anterior.
3. **Registrar token de dispositivo**  
   - `Notifications / Registrar Token` con `token` y `platform`.
4. **Simular evento**  
   - Ejecuta `Reservations / Confirmar Reserva` o `Completar Reserva` para disparar notificaciones (según preferencias).
5. **Listar notificaciones**  
   - `Notifications / Listar Notificaciones`.  
   - Copia un `notificationId`.
6. **Marcar como leída**  
   - `Notifications / Marcar Notificación Como Leída`.
7. **Broadcast (admin)**  
   - `Notifications / Broadcast (Admin)` enviando mensaje al usuario de prueba.  
   - Repite el listado para ver la nueva notificación `system_alert`.

---

### 7. Dashboard & Métricas
- `Analytics / Dashboard Usuario`  
  Valida que refleje los espacios disponibles, sesiones recientes y `canReserve`. Tras penalizaciones manuales (`expire`), verifica que `absenceCount` crezca y `canReserve` pueda volverse `false` (si acumula strikes suficientes).

---

### 8. Limpieza Opcional
- `User Profiles / Eliminar Perfil`.
- `Auth & Users / Eliminar Usuario` (para usuario estándar).  
- `Auth & Users / Eliminar Usuario` (para admin/IoT si sólo eran de prueba).  
- `docker compose down` para detener servicios; elimina volumen si planeas reiniciar desde cero.

---

### Tabla De Variables Postman (referencia rápida)

| Variable           | Cómo obtenerla                           | Uso principal                                  |
|--------------------|------------------------------------------|------------------------------------------------|
| `authTokenAdmin`   | Login con administrador                  | Endpoints protegidos con rol admin             |
| `authToken`        | Login usuario estándar                   | Reservas, notificaciones, perfil               |
| `authTokenIot`     | Login usuario con rol `IOT`              | Activación `IoT Integration`                   |
| `userId`           | Respuesta de creación/registro usuario   | Perfil, sesiones, reservas                     |
| `spaceId`          | Respuesta de crear espacio               | Reservas, activación IoT                       |
| `reservationId`    | Respuesta de crear reserva               | Confirmar/activar/completar/cancelar           |
| `sessionId`        | Respuesta de login (usuario estándar)    | Logout                                         |
| `tokenHash`        | Valor manual usado en `Crear Sesión`     | Consulta `GET /token/{tokenHash}`              |
| `notificationId`   | Respuesta de `Listar Notificaciones`     | `PATCH /{notificationId}/read`                 |

Con estos pasos tendrás cubiertos todos los endpoints expuestos en Swagger. Ajusta payloads según tu escenario (por ejemplo, fechas en formato ISO 8601 UTC) y documenta los resultados relevantes para tus reportes QA.

