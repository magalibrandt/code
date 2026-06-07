# Casos de Uso - eScrims

## Alcance de implementacion

Los casos de uso se ejecutan como llamadas Java directas sobre controller/facade/service. No hay API REST real ni persistencia externa. La autenticacion y algunas integraciones se mantienen como simulaciones de demo.

Los estados de postulacion, estados de reporte y sanciones se modelan como clases de dominio (`EstadoPostulacion`, `EstadoReporte`, `Sancion`).

## CU1: Registrar Usuario

**Actores**: Usuario (jugador)

**Precondiciones**: 
- Usuario no tiene cuenta
- Email no está registrado en el sistema

**Flujo Principal**:
1. Usuario accede a la pantalla de registro
2. Ingresa username, email, contraseña y región
3. Sistema valida formato de email y contraseña (min 8 caracteres)
4. Sistema hashea la contraseña
5. Sistema crea el usuario con estado "Pendiente Verificación"
6. Se envía email de verificación
7. Usuario verifica email
8. Estado del usuario cambia a "Verificado"

**Reglas de Negocio**:
- Email único por usuario
- Username único en el sistema
- Contraseña debe tener mínimo 8 caracteres
- Verificación de email es obligatoria antes de poder postularse

---

## CU2: Autenticar Usuario

**Actores**: Usuario (jugador)

**Precondiciones**:
- Usuario está registrado y verificado
- Sistema está disponible

**Flujo Principal**:
1. Usuario ingresa email y contraseña
2. Sistema valida credenciales
3. Sistema genera token JWT
4. Usuario recibe token con expiración de 24 horas
5. Token se utiliza en todas las request posterior

**Flujos Alternativos**:
- Email no encontrado → Error 404
- Contraseña incorrecta → Error 401, registrar intento fallido
- Múltiples intentos fallidos (>5) → Bloquear cuenta temporalmente

---

## CU3: Crear Scrim

**Actores**: Organizador (jugador que crea)

**Precondiciones**:
- Usuario está autenticado y verificado
- Usuario no tiene sanciones activas
- Tokens válidos

**Flujo Principal**:
1. Organizador accede a "Crear Scrim"
2. Ingresa datos:
   - Juego (Valorant, LoL, CS2, etc.)
   - Formato (5v5, 3v3, 1v1)
   - Región/Servidor
   - Rango mínimo y máximo
   - Latencia máxima permitida
   - Fecha y hora
   - Duración estimada
   - Modalidad (ranked-like, casual, estrategia)
3. Sistema valida datos según reglas del juego
4. Sistema crea scrim en estado "Buscando Jugadores"
5. Sistema publica evento "ScrimCreado"
6. NotificationSubscriber envía notificaciones a usuarios con preferencias coincidentes

**Reglas de Negocio**:
- Mínimo 2 cupos, máximo 10 cupos
- Rango máximo ≥ Rango mínimo
- Fecha debe ser ≥ ahora + 15 minutos
- Latencia máxima: 50-200ms según región

---

## CU4: Postularse a Scrim

**Actores**: Jugador

**Precondiciones**:
- Scrim está en estado "Buscando Jugadores"
- Usuario no tiene postulación previa en este scrim
- Usuario cumple requisitos (rango, latencia, región)
- Usuario no está bajo sanción

**Flujo Principal**:
1. Jugador ve lista de scrims disponibles
2. Jugador selecciona scrim
3. Jugador elige rol deseado (si aplica)
4. Jugador envía postulación
5. Sistema valida requisitos del usuario
6. Sistema crea Postulacion en estado "ACEPTADA"
7. Sistema verifica si cupo está completo
8. Si completo: transición a estado "Lobby Armado"

**Flujos Alternativos**:
- Usuario bajo sanción → Rechazar postulación
- Usuario no cumple rango → Rechazar
- Usuario tiene latencia > máxima → Rechazar
- Cupo lleno → Agregar a lista de espera

---

## CU5: Emparejar y Armar Lobby

**Actores**: Sistema (automático/Organizador)

**Precondiciones**:
- Scrim tiene al menos N postulaciones (N = cupos requeridos)
- Todos cumplen requisitos básicos

**Flujo Principal**:
1. Sistema recopila postulantes aceptadas
2. Sistema selecciona estrategia de matchmaking
3. Algoritmo selecciona jugadores según:
   - Diferencia de rango ≤ 2 (Strategy: ByMMR)
   - Latencia dentro del umbral (Strategy: ByLatency)
   - Compatibilidad de historial (Strategy: ByHistory)
4. Sistema arma dos equipos balanceados
5. Sistema transiciona a estado "Lobby Armado"
6. Notifica a jugadores seleccionados

**Reglas de Negocio**:
- Máximo 1 jugador no-verif por equipo
- Suplentes según disponibilidad
- Balanc eo: intentar MMR similar

---

## CU6: Confirmar Participación

**Actores**: Jugador

**Precondiciones**:
- Scrim en estado "Lobby Armado"
- Jugador tiene postulación aceptada
- Tiempo < 5 minutos antes de inicio

**Flujo Principal**:
1. Sistema envía notificación "Confirma participación"
2. Jugador presiona "Confirmar"
3. Sistema crea registro Confirmacion con confirmado=true
4. Sistema verifica si todos confirmaron
5. Si todos confirman:
   - Transición a estado "Confirmado"
   - Programar transición automática a "EnJuego" a la hora

**Flujos Alternativos**:
- Jugador no confirma en 2 minutos → auto-rechazar (strike)
- Tiempo pasó para confirmar → transición a "Finalizado"

---

## CU7: Iniciar Scrim

**Actores**: Sistema (scheduler/Organizador manual)

**Precondiciones**:
- Scrim en estado "Confirmado"
- Hora de inicio ha llegado
- Todos confirmaron

**Flujo Principal**:
1. Sistema verifica hora = fechaHora del scrim
2. Transiciona a estado "EnJuego"
3. Publica evento "ScrimIniciado"
4. NotificationSubscriber notifica a todos participantes
5. Stats contador comienza

---

## CU8: Finalizar y Cargar Estadísticas

**Actores**: Organizador/Sistema

**Precondiciones**:
- Scrim en estado "EnJuego"
- Tiempo de finalización ha pasado (por cron u organizador)

**Flujo Principal**:
1. Sistema o organizador finaliza scrim
2. Transiciona a "Finalizado"
3. Sistema habilita carga de resultados
4. Organizador carga:
   - Equipo ganador
   - MVP
   - Kills/Assists por jugador
   - Observaciones
5. Sistema calcula MMR deltas (si aplica)
6. Habilita sistema de feedback/rating
7. Usuarios pueden calificar a otros (1-5 estrellas)
8. Comentarios van a "Pendiente Moderación"

**Reglas de Negocio**:
- Máximo 1 hora para cargar estadísticas
- MVP debe ser jugador participante
- Feedback inmoderado es rechazado automáticamente

---

## CU9: Cancelar Scrim

**Actores**: Organizador/Moderador

**Precondiciones**:
- Scrim no está en estado "EnJuego" ni "Finalizado"

**Flujo Principal**:
1. Organizador elige "Cancelar Scrim"
2. Ingresa motivo de cancelación
3. Sistema transiciona a estado "Cancelado"
4. Sistema notifica a todos participantes
5. Si hubo inscripción con fee (opcional): procesar reembolso

**Reglas de Negocio**:
- Cancelación < 1 hora antes: 25% penalty al organizador
- Cancelación < 5 min: 100% penalty + strike

---

## CU10: Enviar Notificaciones

**Actores**: Sistema (Observer)

**Precondiciones**:
- Evento de dominio publicado
- Usuario suscripto

**Flujo Principal**:
1. Evento se publica en DomainEventBus
2. NotificationSubscriber recibe evento
3. Filtra destinatarios según preferencias
4. Construye mensaje según tipo de evento
5. Selecciona canales (push, email, Discord)
6. Envía con Abstract Factory:
   - Dev mode: solo consola
   - Prod mode: Firebase Push, SendGrid Email, Discord webhook
7. Reintentos exponenciales si falla

**Canales**:
- Push: Firebase Cloud Messaging
- Email: JavaMail/SendGrid
- Chat: Discord webhook/bot

---

## CU11: Moderar Reportes

**Actores**: Sistema (Bot), Moderador (Humano)

**Precondiciones**:
- Reporte de conducta creado
- Estado = "PENDIENTE"

**Flujo Principal** (Chain of Responsibility):
1. AutomaticProcessor analiza:
   - SPAM detectado automáticamente → Aplicar ADVERTENCIA
   - No-show/inactividad → Aplicar STRIKE
2. Si no resuelto → BotProcessor:
   - Analiza historial de strikes
   - ≥3 strikes → SUSPENSION_24H
   - ≥5 strikes → BAN_PERMANENTE
3. Si aún no resuelto → HumanModeratorProcessor:
   - Revisa contexto completo
   - Decide sanción final
   - Deja notas de moderador

**Sanciones**:
- NINGUNA
- ADVERTENCIA (no penalidad)
- SUSPENSION_24H
- SUSPENSION_7D
- BAN_PERMANENTE

**Reglas de Negocio**:
- 3 strikes = 7 días cooldown automático
- 5 strikes = revisión para ban permanente
- Reporte falso = -1 reputación al reportador

---

# Historias de Usuario

## HU1: Como jugador, quiero buscar scrims por rango y región

**Criterios de Aceptación**:
- Dado que filtro por "Valorant", "Gold", "LATAM" y latencia 80ms
- Cuando presiono "Buscar"
- Entonces veo lista de scrims disponibles que coinciden
- Y puedo filtrar adicionalemente por fecha/hora

**Casos de Prueba**:
- Escenario 1: Usuario Gold puede ver scrims Gold±1
- Escenario 2: Usuario sin verificar NO ve scrims
- Escenario 3: Búsqueda vacía muestra "Sin resultados"

---

## HU2: Como organizador, quiero crear un scrim 5v5 con límites

**Criterios de Aceptación**:
- Dado que creo scrim con rango [Gold-Plat]
- Cuando usuario Silver se postula
- Entonces sistema rechaza la postulación
- Y muestra "Rango insuficiente"

---

## HU3: Como participante, quiero recibir notificaciones

**Criterios de Aceptación**:
- Dado que me postulo a un scrim
- Cuando el lobby se completa
- Entonces recibo notificación push
- Y recibo email
- Y se publica en mi Discord si está vinculado

---

## HU4: Como moderador, quiero procesar reportes

**Criterios de Aceptación**:
- Dado un reporte de "conducta inapropiada"
- Cuando automático no puede resolver
- Entonces me aparece en cola de moderación
- Y puedo ver contexto completo
- Y puedo aplicar sanciones escalonadas
