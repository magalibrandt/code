# Casos de Uso - eScrims

## Alcance de Implementacion

Los casos de uso se ejecutan como llamadas Java directas sobre `EscrimsFacade`, controllers simulados o servicios de aplicacion. No hay API REST real, Spring Boot ni persistencia externa.

La autenticacion, JWT, verificacion de email, scheduler, reintentos reales, persistencia externa e integraciones productivas se documentan como simulaciones o alcance previsto. Las integraciones externas se modelan con adapters simulados.

`EstadoPostulacion`, `EstadoReporte` y `Sancion` son clases de dominio, no enums.

## CU1: Registrar Usuario

**Actor**: Usuario.

**Implementacion actual**: `AuthController.registrarUsuario(...)` y `EscrimsFacade.crearUsuario(...)` crean usuarios en memoria. El hash de password es una simulacion simple.

**Flujo principal documentado**:
1. Usuario informa username, email, password y region.
2. El sistema crea un `Usuario`.
3. El usuario queda disponible en repositorio en memoria.

**Alcance previsto/simulado**:
- Email unico.
- Username unico.
- Verificacion de email.
- Politicas robustas de password.

## CU2: Autenticar Usuario

**Actor**: Usuario.

**Implementacion actual**: `AuthController.autenticar(...)` devuelve una respuesta simulada con token.

**Alcance previsto/simulado**:
- Validacion real de credenciales.
- JWT productivo.
- Bloqueo por intentos fallidos.

## CU3: Crear Scrim

**Actor**: Organizador.

**Implementacion actual**:
1. Se obtiene el creador desde repositorio en memoria.
2. `ScrimBuilder` construye el `Scrim`.
3. `ScrimService` registra el scrim.
4. `NotificationSubscriber` puede registrar el scrim para reaccionar a eventos.

**Reglas implementadas**:
- Fecha no nula y no pasada.
- Latencia en rango valido.
- Cupos en rango permitido por builder.
- Modalidad valida.

**Alcance previsto/simulado**:
- Reglas especificas por juego.
- Validacion productiva de usuario autenticado.
- Persistencia externa.

## CU4: Postularse a Scrim

**Actor**: Jugador.

**Implementacion actual**:
1. El usuario se postula mediante `Scrim.postular(...)` o `ScrimService.postularseAScrim(...)`.
2. El estado actual del `Scrim` decide si la accion es valida.
3. Se crea una `Postulacion` con `EstadoPostulacion`.
4. Al completar cupos, el scrim pasa a `LobbyArmadoState`.

**Alcance previsto/simulado**:
- Lista de espera.
- Validacion completa de rango y latencia antes de aceptar.
- Bloqueo por sanciones en todas las rutas.

## CU5: Emparejar y Armar Lobby

**Actor**: Sistema/organizador.

**Implementacion actual**:
1. `ScrimService` usa una `MatchmakingStrategy`.
2. La estrategia selecciona candidatos.
3. `Scrim.ejecutarMatchmaking(...)` distribuye jugadores entre `Equipo A` y `Equipo B`.

**Estrategias implementadas**:
- `ByMMRStrategy`
- `ByLatencyStrategy`
- `ByHistoryStrategy`

## CU6: Confirmar Participacion

**Actor**: Jugador.

**Implementacion actual**:
1. `Scrim.confirmar(...)` delega en el estado actual.
2. Se registra una `Confirmacion`.
3. Cuando todos confirman, el scrim pasa a `ConfirmadoState`.

**Alcance previsto/simulado**:
- Timeouts de confirmacion.
- Penalidad automatica por no confirmar.
- Recordatorios programados.

## CU7: Iniciar Scrim

**Actor**: Organizador/sistema.

**Implementacion actual**:
- `Scrim.iniciar(...)` transiciona de `ConfirmadoState` a `EnJuegoState`.
- Se publica `ScrimStateChangedEvent`.

**Alcance previsto/simulado**:
- Inicio por scheduler segun `fechaHora`.

## CU8: Finalizar y Cargar Estadisticas

**Actor**: Organizador/sistema.

**Implementacion actual**:
- `Scrim.finalizar(...)` transiciona a `FinalizadoState`.
- `ScrimService.finalizarScrim(...)` puede asociar `Estadistica`.

**Alcance previsto/simulado**:
- Carga completa de resultados por UI/API.
- Calculo real de MMR.
- Feedback/rating productivo.

## CU9: Cancelar Scrim

**Actor**: Organizador.

**Implementacion actual**:
- `Scrim.cancelar(...)` delega en el estado actual.
- Los estados validos transicionan a `CanceladoState`.

**Alcance previsto/simulado**:
- Reembolsos.
- Penalidades por cancelacion tardia.
- Mensajes con motivo de cancelacion.

## CU10: Enviar Notificaciones

**Actor**: Sistema.

**Implementacion actual**:
1. Se publica un `DomainEvent` en `DomainEventBus`.
2. `NotificationSubscriber` recibe el evento.
3. Se usan `NotifierFactory` y `Notifier` para enviar por canales simulados.

**Canales simulados**:
- Push.
- Email.
- Discord.

**Alcance previsto/simulado**:
- Firebase real.
- SendGrid real.
- Discord webhook real.
- Reintentos exponenciales.

## CU11: Moderar Reportes

**Actor**: Sistema/moderador.

**Implementacion actual**:
1. `ScrimService.procesarReporte(...)` arma una cadena.
2. `AutomaticProcessor`, `BotProcessor` y `HumanModeratorProcessor` intentan resolver el reporte.
3. `ReporteConducta` usa `EstadoReporte` y `Sancion`.

**Alcance previsto/simulado**:
- Cola real de moderacion.
- Auditoria productiva.
- Moderador humano real.

## Historias de Usuario

Las historias expresan objetivos funcionales del producto. Algunas condiciones describen comportamiento futuro o simulado.

### HU1: Buscar scrims por rango y region

Como jugador, quiero buscar scrims por juego, rango y region para encontrar partidas compatibles.

### HU2: Crear un scrim con limites

Como organizador, quiero crear un scrim con formato, region, rango y latencia maxima.

### HU3: Recibir notificaciones

Como participante, quiero recibir avisos cuando mi postulacion sea aceptada o cambie el estado del scrim.

### HU4: Procesar reportes

Como moderador, quiero que los reportes pasen por una cadena de procesamiento automatico, bot y humano.

