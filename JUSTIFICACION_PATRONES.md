# Justificacion de Patrones de Diseno - eScrims

## Nota de Consistencia

El proyecto toma como contrato el diagrama de clases final. `EstadoPostulacion`, `EstadoReporte` y `Sancion` son clases de dominio con comportamiento propio, no enums.

Las integraciones externas estan simuladas. Cuando se mencionan servicios como Discord, SendGrid o iCal, se hace referencia a adapters de infraestructura usados como demostracion, no a integraciones productivas reales.

## 1. Patron State

### Problema

El ciclo de vida de un scrim tiene estados con reglas distintas para las mismas operaciones: postular, confirmar, iniciar, finalizar o cancelar.

### Solucion

- Contexto: `Scrim`.
- Interface: `ScrimState`, en `domain.state`.
- Estados concretos: `BuscandoJugadoresState`, `LobbyArmadoState`, `ConfirmadoState`, `EnJuegoState`, `FinalizadoState`, `CanceladoState`.

`Scrim` delega el comportamiento al estado actual, evitando condicionales extensos y manteniendo cohesion en cada clase de estado.

## 2. Patron Strategy

### Problema

El matchmaking puede variar por rango, latencia o historial.

### Solucion

`ScrimService` depende de la abstraccion `MatchmakingStrategy`. Las implementaciones actuales son:
- `ByMMRStrategy`
- `ByLatencyStrategy`
- `ByHistoryStrategy`

Esto permite cambiar la estrategia en tiempo de ejecucion sin modificar el servicio.

## 3. Patron Observer

### Problema

Los cambios de estado y postulaciones aceptadas deben generar notificaciones sin acoplar el dominio a infraestructura.

### Solucion

- `DomainEventBus` como sujeto observable y singleton.
- `Subscriber` como interfaz de observador.
- `NotificationSubscriber` como observador concreto ubicado en `application.subscribers`.
- `ScrimStateChangedEvent` y `PostulacionAceptadaEvent` como eventos de dominio.

Las notificaciones salen por `NotificationSubscriber`, que usa `NotifierFactory` y `Notifier`.

## 4. Patron Abstract Factory

### Problema

El sistema necesita crear notificadores de distintos canales sin acoplar clientes a clases concretas.

### Solucion

`NotifierFactory` crea `PushNotifier`, `EmailNotifier` y `DiscordNotifier`. `DevNotifierFactory` y `ProdNotifierFactory` representan familias de creacion. En esta version el envio se simula por consola.

## 5. Patron Facade

### Problema

El cliente no deberia conocer ni coordinar directamente servicios, bus de eventos, comandos, notificaciones y moderacion.

### Solucion

`EscrimsFacade` ofrece una interfaz unificada y coordina:
- `ScrimService`
- `DomainEventBus`
- `NotificationSubscriber`
- `NotifierFactory`
- `CommandInvoker`
- `ScrimBuilder`
- Moderacion a traves de `ScrimService` y `ReportProcessor`

La fachada no depende directamente de `DiscordAdapter`, `SendGridAdapter` ni `ICalAdapter`, y no expone operaciones directas de conexion o envio contra esos adapters.

Las notificaciones se canalizan por Observer + Abstract Factory, no por llamadas directas a adapters desde la fachada.

## 6. Patron Builder

### Problema

Crear un `Scrim` requiere varios datos y validaciones.

### Solucion

`ScrimBuilder` encapsula la construccion fluida de scrims y valida rangos, latencia, fecha, cupos y modalidad.

## 7. Patron Command

### Problema

Algunas acciones del lobby deben encapsularse como operaciones reversibles.

### Solucion

`ScrimCommand` define la interfaz. `AsignarRolCommand`, `SwapJugadoresCommand` e `InvitarJugadorCommand` implementan acciones concretas. `CommandInvoker` ejecuta, deshace y rehace comandos.

## 8. Patron Chain of Responsibility

### Problema

Los reportes de conducta pueden resolverse automaticamente, por bot o por revision humana.

### Solucion

`ReportProcessor` define la cadena. `AutomaticProcessor`, `BotProcessor` y `HumanModeratorProcessor` procesan o derivan el reporte al siguiente handler.

## 9. Patron Adapter

### Problema

Las integraciones externas tienen interfaces propias y no deben contaminar el dominio.

### Solucion

`ExternalServiceAdapter` abstrae integraciones. `DiscordAdapter`, `SendGridAdapter` e `ICalAdapter` encapsulan servicios externos simulados.

`DemoCompleta` instancia adapters directamente para mostrar el patron en ejecucion. Ese uso es demostrativo y no representa el flujo limpio de cliente, que debe pasar por `EscrimsFacade` y servicios de aplicacion.

## Principios SOLID y GRASP

- SRP: cada clase concentra una responsabilidad.
- OCP: nuevos estados, estrategias, comandos, suscriptores o procesadores pueden agregarse sin reescribir el flujo principal.
- DIP: servicios y suscriptores dependen de abstracciones como `MatchmakingStrategy`, `Subscriber`, `NotifierFactory` y `Notifier`.
- Low Coupling: el dominio no conoce infraestructura de notificaciones.
- High Cohesion: estados, estrategias, comandos y processors concentran reglas relacionadas.
- Controller/Facade: `ScrimService` coordina casos de uso y `EscrimsFacade` simplifica el acceso del cliente.

## Resumen

| Patron | Ubicacion | Proposito |
|---|---|---|
| State | `domain.state` | Ciclo de vida de `Scrim` |
| Strategy | `domain.strategy` | Matchmaking intercambiable |
| Observer | `domain.events`, `application.subscribers` | Eventos y notificaciones desacopladas |
| Abstract Factory | `infrastructure.notifications` | Creacion de notificadores |
| Facade | `application.facade` | Interfaz unificada de aplicacion |
| Builder | `application.builder` | Construccion validada de scrims |
| Command | `domain.command` | Acciones reversibles |
| Chain of Responsibility | `domain.moderacion` | Moderacion escalonada |
| Adapter | `infrastructure.adapters` | Integraciones externas simuladas |
