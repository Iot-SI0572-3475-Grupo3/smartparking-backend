## Guía De Pruebas Manuales (Swagger / Postman)

> Referencia completa de endpoints del backend SmartParking para ejecuciones manuales con Swagger UI o Postman.

### Preparativos
- **Base URL local**: `http://localhost:8080`
- **Header tras autenticación**: `Authorization: Bearer <TOKEN>`
- Variables útiles (Postman):
  - `authToken` (usuario estándar)
  - `authTokenAdmin` (usuario administrador)
  - `authTokenIot` (usuario con rol `IOT`, opcional)
  - `userId`, `spaceId`, `reservationId`, `sessionId`, `notificationId`
- Ejecuta `docker compose up --build` antes de iniciar y mantiene la consola abierta para revisar logs.

---

### Agrupación de Endpoints
- **Auth & Users**: registro, login, CRUD de usuarios y logout.
- **User Profiles**: operaciones 1:1 sobre `user_profiles`.
- **User Sessions**: administración de sesiones activas y tokens.
- **Reservations**: ciclo completo de reservas y expiraciones manuales.
- **IoT Integration**: activación automática por espacio.
- **Parking Spaces**: creación, consulta y actualización de espacios IoT.
- **Notifications**: preferencias, tokens, bandeja y broadcast.
- **Analytics**: dashboard de usuario.

---

### 1. Autenticación y Gestión de Usuarios (`/api/auth/users`)
- **POST /register/university-member**  
  Crea usuario con rol `university_member`.  
  ```json
  { "firstName": "Lucia", "lastName": "Prado", "email": "lucia@example.com", "password": "Passw0rd!" }
  ```
- **POST /register/administrator**  
  Igual que el anterior pero asigna rol `administrator`; úsalo para obtener `authTokenAdmin`.
- **POST /** _(requiere rol `administrator`)_  
  Crea usuarios manualmente.  
  ```json
  { "email": "user@example.com", "passwordHash": "Temp123!", "role": "university_member", "status": "active" }
  ```
- **POST /login**  
  Autentica y devuelve `token` + `sessionId`. Guarda ambos en variables.
- **POST /logout/{sessionId}** _(requiere autenticación)_  
  Invalida la sesión activa; responde `204`.
- **GET /** _(requiere autenticación)_  
  Lista todos los usuarios.
- **GET /{userId}** _(requiere autenticación)_  
  Recupera datos básicos del usuario.
- **PUT /{userId}** _(requiere autenticación)_  
  Actualiza correo, contraseña y estado.  
  ```json
  { "email": "nuevo@example.com", "passwordHash": "Opcional123", "role": "administrator", "status": "active" }
  ```
- **DELETE /{userId}** _(requiere rol `administrator`)_  
  Elimina definitivamente al usuario.

### 2. Perfiles de Usuario (`/api/user-profiles`)
- **POST /{userId}**  
  Crea perfil 1:1 si no existe (nombre, apellido).  
  ```json
  { "firstName": "Lucia", "lastName": "Prado" }
  ```
- **GET /{userId}**  
  Obtiene la ficha de perfil.
- **PUT /{userId}**  
  Actualiza nombre/apellido.
- **DELETE /{userId}**  
  Borra el perfil asociado.

### 3. Sesiones de Usuario (`/api/user-sessions`)
- **POST /{userId}**  
  Registra sesión manual con un `tokenHash` (útil para pruebas).
- **GET /active/{userId}**  
  Lista sesiones activas del usuario.
- **GET /token/{tokenHash}**  
  Recupera sesión a partir del hash.
- **DELETE /{sessionId}**  
  Invalida la sesión indicada.

### 4. Reservas (`/api/v1/reservation`)
- **POST /** _(requiere autenticación)_  
  Crea reserva para un espacio disponible.  
  ```json
  {
    "spaceId": "{{spaceId}}",
    "startTime": "2025-11-13T14:30:00",
    "vehicleInfo": "{\"plate\":\"ABC123\",\"color\":\"Rojo\"}",
    "specialRequirements": "Cerca de la entrada"
  }
  ```
- **POST /{reservationId}/confirm** _(requiere autenticación)_  
  Cambia la reserva de `pending` a `confirmed`.
- **POST /{reservationId}/activate** _(requiere autenticación)_  
  Simula llegada del conductor dentro de la ventana permitida.
- **POST /{reservationId}/complete** _(requiere autenticación)_  
  Marca la reserva `active` como `completed`, calcula costo y libera el espacio.
- **POST /{reservationId}/cancel** _(requiere autenticación)_  
  Cancela si faltan ≥15 min para el inicio.  
  ```json
  { "reason": "Cambio de planes" }
  ```
- **POST /{reservationId}/expire** _(requiere autenticación)_  
  Fuerza la expiración (útil para soporte/operaciones).
- **GET /active** _(requiere autenticación)_  
  Devuelve la reserva activa más reciente (`minutesUntilArrival`, `canCancel`).
- **GET /history** _(requiere autenticación)_  
  Listado de reservas finalizadas (`completed`, `cancelled`, `expired`).

### 5. Integración IoT (`/api/v1/iot/reservation`)
- **POST /activate/{spaceId}** _(requiere rol `IOT`)_  
  Activa automáticamente la reserva confirmada asociada al espacio y registra un `ArrivalEvent`.  
  Usa `authTokenIot` en el header.

### 6. Espacios de Estacionamiento (`/api/v1/space-iot/parking-spaces`)
- **GET /** _(requiere autenticación)_  
  Lista todos los espacios.
- **GET /status/{status}** _(requiere autenticación)_  
  Filtra por `available | reserved | occupied | maintenance`.
- **POST /** _(requiere rol `administrator`)_  
  Crea nuevo espacio.  
  ```json
  { "code": "A101", "status": "available" }
  ```
- **PUT /{spaceId}** _(requiere rol `administrator`)_  
  Actualiza código y estado.  
  ```json
  { "code": "A101B", "status": "maintenance" }
  ```

### 7. Notificaciones (`/api/v1/notifications`)
- **GET /preferences** _(requiere autenticación)_  
  Obtiene preferencias activas del usuario.
- **PUT /preferences** _(requiere autenticación)_  
  Guarda o crea preferencias.  
  ```json
  [
    {
      "notificationType": "reservation_confirmed",
      "channel": "push",
      "isEnabled": true,
      "quietHoursStart": "23:00",
      "quietHoursEnd": "06:00"
    }
  ]
  ```
- **POST /token** _(requiere autenticación)_  
  Registra/dispara un token de dispositivo.  
  ```json
  { "token": "fcm-token", "platform": "android" }
  ```
- **GET /** _(requiere autenticación)_  
  Lista notificaciones recientes (paginado con `page` y `size`).
- **PATCH /{notificationId}/read** _(requiere autenticación)_  
  Marca una notificación como leída (`delivered`).
- **POST /broadcast** _(requiere rol `administrator`)_  
  Envía notificación masiva.  
  ```json
  {
    "userIds": ["{{userId}}"],
    "type": "system_alert",
    "data": { "title": "Mantenimiento", "message": "El estacionamiento cerrará a las 22:00" }
  }
  ```

### 8. Analítica (`/api/v1/reservation/dashboard`)
- **GET /** _(requiere autenticación)_  
  Devuelve resumen de espacios disponibles, sesiones recientes, contador de ausencias y si el usuario puede reservar.

---

### Consejos Finales
- Mantén tokens vigentes: si reinicias el backend deberás hacer login de nuevo.
- Valida respuestas en consola/logs para confirmar cambios en la base.
- Para rearmar datos desde cero, borra el volumen `postgres_data` y vuelve a levantar Docker.
- Documenta cada prueba con request/response y tiempos de respuesta para tus reportes QA.