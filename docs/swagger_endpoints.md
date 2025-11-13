## Guía De Endpoints SmartParking (Swagger)

### Auth & Users (`/api/auth/users`)
- `POST /register/university-member`  
  Crea un usuario con rol `university_member` y genera el perfil asociado.
- `POST /register/administrator`  
  Alta de usuarios administradores para tareas operativas.
- `POST /` *(requiere rol administrator)*  
  Crea usuarios manualmente (correo, rol, estado y contraseña).
- `POST /login`  
  Autentica credenciales y devuelve un JWT (`token`) más `sessionId`.
- `POST /logout/{sessionId}`  
  Marca la sesión como inactiva para cerrar la sesión del usuario.
- `GET /` *(requiere autenticación)*  
  Lista todos los usuarios registrados.
- `GET /{userId}` *(requiere autenticación)*  
  Obtiene los datos básicos del usuario.
- `PUT /{userId}` *(requiere autenticación)*  
  Actualiza correo, contraseña (si se envía), rol y estado.
- `DELETE /{userId}` *(requiere rol administrator)*  
  Elimina definitivamente al usuario.

### User Profiles (`/api/user-profiles`)
- `POST /{userId}`  
  Crea el perfil 1:1 (nombre y apellido) si aún no existe.
- `GET /{userId}`  
  Recupera la ficha personal del usuario.
- `PUT /{userId}`  
  Actualiza nombre y apellidos.
- `DELETE /{userId}`  
  Elimina el perfil asociado.

### User Sessions (`/api/user-sessions`)
- `POST /{userId}`  
  Registra manualmente una sesión (hash de token).
- `GET /active/{userId}`  
  Lista las sesiones activas del usuario.
- `GET /token/{tokenHash}`  
  Busca una sesión a partir del hash almacenado.
- `DELETE /{sessionId}`  
  Invalida la sesión especificada.

### Parking Spaces (`/api/v1/space-iot/parking-spaces`)
- `POST /` *(requiere rol administrator)*  
  Crea un espacio con código único y estado inicial.
- `GET /` *(requiere autenticación)*  
  Devuelve todos los espacios registrados.
- `GET /status/{status}` *(requiere autenticación)*  
  Filtra por estado (`available`, `reserved`, `occupied`, `maintenance`).
- `PUT /{spaceId}` *(requiere rol administrator)*  
  Actualiza código y/o estado del espacio.

### Reservations (`/api/v1/reservation`)
- `POST /` *(requiere autenticación)*  
  Crea una reserva pendiente verificando disponibilidad.
- `POST /{reservationId}/confirm` *(requiere autenticación)*  
  Cambia la reserva a `confirmed` si sigue vigente.
- `POST /{reservationId}/activate` *(requiere autenticación)*  
  Marca la llegada del usuario dentro de la ventana permitida.
- `POST /{reservationId}/complete` *(requiere autenticación)*  
  Completa una reserva activa, calcula costo y libera el espacio.
- `POST /{reservationId}/cancel` *(requiere autenticación)*  
  Cancela si faltan 15 minutos o más para el inicio.
- `POST /{reservationId}/expire` *(requiere autenticación)*  
  Fuerza la expiración manual (soporte/operaciones).
- `GET /active` *(requiere autenticación)*  
  Muestra la reserva activa o próxima (incluye `minutesUntilArrival` y `canCancel`).
- `GET /history` *(requiere autenticación)*  
  Historial de reservas finalizadas, canceladas o expiradas.

### IoT Integration (`/api/v1/iot/reservation`)
- `POST /activate/{spaceId}` *(requiere rol IOT)*  
  Activa automáticamente la reserva confirmada para el espacio y registra un `ArrivalEvent`.

### Notifications (`/api/v1/notifications`)
- `GET /preferences` *(requiere autenticación)*  
  Consulta las preferencias actuales.
- `PUT /preferences` *(requiere autenticación)*  
  Crea o actualiza preferencias (canal, quiet-hours, habilitación).
- `POST /token` *(requiere autenticación)*  
  Registra el token de dispositivo (push/web).
- `GET /` *(requiere autenticación)*  
  Lista las notificaciones del usuario con paginación (`page`, `size`).
- `PATCH /{notificationId}/read` *(requiere autenticación)*  
  Marca una notificación como leída (`delivered`).
- `POST /broadcast` *(requiere rol administrator)*  
  Envía una notificación masiva (`system_alert`, etc.) a varios usuarios.

### Analytics (`/api/v1/reservation/dashboard`)
- `GET /` *(requiere autenticación)*  
  Devuelve el dashboard del usuario (espacios disponibles, sesiones recientes, contador de ausencias, `canReserve`).

---

### Resumen Por Grupo
- **Auth & Users**: registro, login, logout y administración de usuarios.
- **User Profiles**: gestión de datos personales extendidos.
- **User Sessions**: control y revocación de sesiones JWT almacenadas.
- **Parking Spaces**: administración del parque IoT de plazas.
- **Reservations**: ciclo completo de reservas, desde creación hasta cierre.
- **IoT Integration**: activación automática basada en dispositivos físicos.
- **Notifications**: preferencias, tokens de dispositivo, bandeja y broadcast.
- **Analytics**: métricas clave y estado actual del usuario.

