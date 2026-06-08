# Diagrama de Estados - Scrim

## Nota de Alcance

Algunas transiciones automaticas por scheduler, timeouts, reembolsos y penalidades estan documentadas como reglas de negocio previstas, pero en esta version se simulan o no estan implementadas completamente.

La implementacion real usa el patron State: `Scrim` es el contexto y delega en `ScrimState`, ubicado en `domain.state`.

## Estados Implementados

### 1. Buscando Jugadores

Estado inicial de un scrim.

Acciones implementadas:
- `postular(usuario, rolDeseado)`: crea una postulacion aceptada.
- `cancelar()`: transiciona a `CanceladoState`.

Transicion implementada:
- Cuando el cupo se completa, pasa a `LobbyArmadoState`.

### 2. Lobby Armado

El cupo esta completo y se esperan confirmaciones.

Acciones implementadas:
- `confirmar(usuario)`: registra confirmacion.
- `cancelar()`: transiciona a `CanceladoState`.

Transicion implementada:
- Cuando todos confirman, pasa a `ConfirmadoState`.

### 3. Confirmado

Todos los participantes confirmaron.

Acciones implementadas:
- `iniciar()`: transiciona a `EnJuegoState`.
- `cancelar()`: transiciona a `CanceladoState`.

Nota: el inicio automatico por scheduler esta documentado como regla prevista, pero no existe scheduler productivo en esta version.

### 4. En Juego

El scrim esta en progreso.

Acciones implementadas:
- `finalizar()`: transiciona a `FinalizadoState`.

Nota: la finalizacion automatica por duracion estimada esta documentada como regla prevista.

### 5. Finalizado

Estado terminal. Permite registrar estadisticas desde el flujo de aplicacion, pero no vuelve a estados anteriores.

### 6. Cancelado

Estado terminal. Reembolsos, penalidades y timeouts se documentan como reglas previstas o simuladas, no como integraciones productivas.

## Estados Concretos

Cada estado implementa `ScrimState`:

- `BuscandoJugadoresState`
- `LobbyArmadoState`
- `ConfirmadoState`
- `EnJuegoState`
- `FinalizadoState`
- `CanceladoState`

`Scrim` delega:

```java
public void postular(Usuario usuario, String rolDeseado) {
    estado.postular(this, usuario, rolDeseado);
}

public void confirmar(Usuario usuario) {
    estado.confirmar(this, usuario);
}
```

## Transiciones Principales

| Estado actual | Accion o condicion | Nuevo estado | Implementacion |
|---|---|---|---|
| Buscando Jugadores | `postular` sin completar cupo | Buscando Jugadores | Implementado |
| Buscando Jugadores | cupo completo | Lobby Armado | Implementado |
| Buscando Jugadores | `cancelar` | Cancelado | Implementado |
| Lobby Armado | `confirmar` sin completar confirmaciones | Lobby Armado | Implementado |
| Lobby Armado | todos confirmaron | Confirmado | Implementado |
| Lobby Armado | `cancelar` | Cancelado | Implementado |
| Confirmado | `iniciar` | En Juego | Implementado |
| Confirmado | scheduler por fecha/hora | En Juego | Previsto/simulado |
| Confirmado | `cancelar` | Cancelado | Implementado |
| En Juego | `finalizar` | Finalizado | Implementado |
| En Juego | timeout por duracion | Finalizado | Previsto/simulado |
| Finalizado | - | Terminal | Implementado |
| Cancelado | - | Terminal | Implementado |

## Eventos y Notificaciones

Las transiciones publican `ScrimStateChangedEvent`. Las postulaciones aceptadas publican `PostulacionAceptadaEvent`.

El flujo de notificacion es:

```text
Scrim / State
  -> DomainEventBus
  -> NotificationSubscriber
  -> NotifierFactory
  -> Notifier
```

`DomainEventBus` y `Subscriber` estan en `domain.events`. `NotificationSubscriber` esta en `application.subscribers`.

Las notificaciones reales se simulan por consola.

## Reglas Previstas

Las siguientes reglas se conservan como documentacion de negocio futura o simulada:

- Cancelacion automatica por inactividad.
- Recordatorios por scheduler.
- Transicion automatica por fecha/hora.
- Reembolsos.
- Penalidades por abandono o cancelacion tardia.
- Calculo de MMR posterior al scrim.

