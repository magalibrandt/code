# TPO Final - eScrims: Plataforma de Organizacion de Scrims

## Descripcion

eScrims es una plataforma academica para organizar scrims de eSports. El proyecto modela creacion y busqueda de scrims, postulaciones, armado de lobby, confirmaciones, ciclo de vida del scrim, notificaciones simuladas y moderacion de reportes.

El foco principal del trabajo es el diseno orientado a objetos y la aplicacion de patrones de diseno. No hay API REST real, Spring Boot ni persistencia externa: los casos se ejecutan como llamadas Java directas y los repositorios se mantienen en memoria.

## Alcance de Implementacion

- `ScrimController` es un controller simulado para operaciones de scrims, sin framework web real.
- `AuthController` concentra `registrarUsuario(...)` y `autenticar(...)`; la autenticacion es simulada.
- `PatternTests` es una clase ejecutable con `main`; no usa JUnit y valida con `AssertionError`.
- `EstadoPostulacion`, `EstadoReporte` y `Sancion` son clases de dominio, no enums.
- Las integraciones externas son simuladas.
- `Main` y `EjemploConFacade` muestran el uso limpio de `EscrimsFacade`.
- `DemoCompleta` instancia algunos subsistemas directamente para demostrar patrones en ejecucion; no representa el flujo limpio de uso de la aplicacion.

## Arquitectura

```text
src/main/java/com/escrims/
├── application/
│   ├── builder/
│   │   └── ScrimBuilder.java
│   ├── controller/
│   │   ├── AuthController.java
│   │   └── ScrimController.java
│   ├── facade/
│   │   └── EscrimsFacade.java
│   ├── service/
│   │   └── ScrimService.java
│   └── subscribers/
│       └── NotificationSubscriber.java
├── domain/
│   ├── command/
│   ├── events/
│   │   ├── DomainEventBus.java
│   │   ├── Subscriber.java
│   │   ├── ScrimStateChangedEvent.java
│   │   └── PostulacionAceptadaEvent.java
│   ├── model/
│   ├── moderacion/
│   ├── state/
│   └── strategy/
├── infrastructure/
│   ├── adapters/
│   └── notifications/
├── Main.java
├── DemoCompleta.java
└── EjemploConFacade.java
```

## Patrones de Diseno Implementados

### State

`Scrim` es el contexto del patron State y delega su comportamiento en `ScrimState`, ubicado en `domain.state`.

Estados concretos:
- `BuscandoJugadoresState`
- `LobbyArmadoState`
- `ConfirmadoState`
- `EnJuegoState`
- `FinalizadoState`
- `CanceladoState`

### Strategy

`ScrimService` usa `MatchmakingStrategy` para seleccionar jugadores. Las estrategias implementadas son `ByMMRStrategy`, `ByLatencyStrategy` y `ByHistoryStrategy`.

### Observer

Las notificaciones se canalizan mediante:
- `DomainEventBus`
- `Subscriber`
- `NotificationSubscriber`
- `DomainEvent`
- `ScrimStateChangedEvent`
- `PostulacionAceptadaEvent`

`DomainEventBus` y `Subscriber` estan en `domain.events`. `NotificationSubscriber` esta en `application.subscribers`.

### Facade

`EscrimsFacade` es el punto de entrada limpio para el cliente. Coordina:
- `ScrimService`
- `DomainEventBus`
- `NotificationSubscriber`
- `NotifierFactory`
- `CommandInvoker`
- `ScrimBuilder`

La fachada no depende directamente de `DiscordAdapter`, `SendGridAdapter` ni `ICalAdapter`. Las notificaciones pasan por Observer + Abstract Factory. `DemoCompleta` muestra adapters directamente solo como demostracion del patron Adapter.

### Abstract Factory

`NotifierFactory` crea notificadores (`PushNotifier`, `EmailNotifier`, `DiscordNotifier`) para distintos entornos (`DevNotifierFactory`, `ProdNotifierFactory`). La implementacion actual imprime mensajes simulados por consola.

### Command

`CommandInvoker` ejecuta comandos reversibles que implementan `ScrimCommand`: `AsignarRolCommand`, `SwapJugadoresCommand` e `InvitarJugadorCommand`.

### Chain of Responsibility

`ReportProcessor` define la cadena de moderacion. Los handlers concretos son `AutomaticProcessor`, `BotProcessor` y `HumanModeratorProcessor`.

### Adapter

`ExternalServiceAdapter`, `DiscordAdapter`, `SendGridAdapter` e `ICalAdapter` representan integraciones externas simuladas. Su uso directo aparece en `DemoCompleta` para mostrar el patron en ejecucion.

### Builder

`ScrimBuilder` permite construir `Scrim` con validaciones de rango, latencia, fecha, cupos y modalidad.

## Compilacion y Ejecucion

### Windows

```cmd
compile.bat
run.bat
```

### Linux/Mac

```bash
chmod +x compile.sh run.sh
./compile.sh
./run.sh
```

### Ejecucion manual

```bash
java -cp out com.escrims.Main
java -cp out com.escrims.EjemploConFacade
java -cp out com.escrims.DemoCompleta
java -cp out com.escrims.tests.PatternTests
```

## Salida Esperada de Main

`Main` muestra un flujo simple:
1. Uso de `EscrimsFacade`.
2. Creacion de usuarios y scrim.
3. Transiciones del patron State.
4. Cambio de estrategia de matchmaking.
5. Publicacion de eventos por Observer.

Las notificaciones aparecen por consola porque las integraciones son simuladas.

## Casos de Uso

Ver [CASOS_DE_USO.md](CASOS_DE_USO.md). Los casos se documentan como flujos de aplicacion ejecutados por llamadas Java directas, no como endpoints REST reales.

## Diagrama de Estados

Ver [DIAGRAMA_ESTADOS.md](DIAGRAMA_ESTADOS.md).

## Documentacion de Patrones

Ver [JUSTIFICACION_PATRONES.md](JUSTIFICACION_PATRONES.md).

## Instrucciones Detalladas

Ver [INSTRUCCIONES_COMPILACION.md](INSTRUCCIONES_COMPILACION.md).

## Integrantes

- Agustin Arguello - LU 1167126
- Taiel Vinograd - LU 1167839
- Melinda Selles - LU 1124972
- German Schettini - LU 1163057
- Magali Brandt - LU 1167149

## Fecha de Entrega

18/06/2026

