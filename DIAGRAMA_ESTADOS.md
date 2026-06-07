# Diagrama de Estados - Scrim

## Nota de implementacion

El ciclo de vida de `Scrim` usa el patron State con clases concretas en `domain.state`. Los estados de `Postulacion` y `ReporteConducta` se representan con las clases de dominio `EstadoPostulacion` y `EstadoReporte`.

## Descripción

El ciclo de vida de un Scrim sigue un patrón de máquina de estados con 6 estados posibles y transiciones claramente definidas.

## Estados

### 1. BUSCANDO JUGADORES
- **Descripción**: Estado inicial cuando se crea el scrim
- **Acciones Permitidas**:
  - `postular(usuario, rol)` → Crea postulación
  - `cancelar()` → Transición a CANCELADO
- **Transiciones Automáticas**:
  - Si postulaciones.size() == cuposTotales → LOBBY_ARMADO
- **Timeout**: Cancelación automática si pasan 24 horas sin cambios

### 2. LOBBY ARMADO
- **Descripción**: Se alcanzó el cupo, esperando confirmaciones
- **Acciones Permitidas**:
  - `confirmar(usuario)` → Registra confirmación
  - `cancelar()` → Transición a CANCELADO
  - `removeUsuario(usuario)` → Vuelve a BUSCANDO_JUGADORES
- **Transiciones Automáticas**:
  - Si confirmaciones.size() == cuposTotales → CONFIRMADO
- **Timeout**: 30 minutos para confirmar, si vence → CANCELADO

### 3. CONFIRMADO
- **Descripción**: Todos confirmaron, esperando hora de inicio
- **Acciones Permitidas**:
  - `cancelar()` → Transición a CANCELADO
  - `iniciar()` (manual) → Transición a EN_JUEGO
- **Transiciones Automáticas**:
  - Si fecha/hora alcanzada → EN_JUEGO (scheduler)
- **Timeout**: 5 minutos antes de hora → notificar

### 4. EN JUEGO
- **Descripción**: El scrim está en progreso
- **Acciones Permitidas**:
  - `finalizar()` → Transición a FINALIZADO
- **Transiciones Automáticas**:
  - Si fechaHora + duracion alcanzada → FINALIZADO
- **Timeout**: Duración estimada del scrim

### 5. FINALIZADO
- **Descripción**: Scrim concluido, cargando estadísticas
- **Acciones Permitidas**:
  - `cargarEstadisticas(stats)`
  - `cargarFeedback(usuario, rating, comentario)`
- **No hay Transiciones**:
  - Este es un estado terminal
  - Se pueden seguir cargando datos después

### 6. CANCELADO
- **Descripción**: Scrim fue cancelado
- **Razones Posibles**:
  - Organizador canceló manualmente
  - No hubo suficientes postulaciones en 24h
  - No todos confirmaron en el tiempo límite
  - Timeout por inactividad
- **No hay Transiciones**:
  - Este es un estado terminal
  - Se procesan reembolsos si aplica
  - Se registran strikes si aplica

## Transiciones Visuales (ASCII)

```
                                    ┌──────────────────────────────────┐
                                    │                                  │
                                    │  [BUSCANDO_JUGADORES]            │
                                    │  (Estado Inicial)                 │
                                    │                                  │
                                    └────────┬──────────────────────────┘
                                             │
                        (cupo completo)      │      (cancelar)
                        [postulaciones       │      [cancelar()]
                         == cupos]           │
                                             │
                                    ┌────────▼─────────┐
                                    │                  │
                                    │ [LOBBY_ARMADO]   │◄──── (remove usuario)
                                    │ (Espera confirm) │      (vuelve a buscar)
                                    │                  │
                                    └────────┬─────────┘
                                             │
                        (todos confirman)    │      (cancelar)
                        [confirmaciones      │      [cancelar()]
                         == cupos]           │
                                             │
                                    ┌────────▼──────────┐
                                    │                   │
                                    │ [CONFIRMADO]      │
                                    │ (Espera inicio)   │
                                    │                   │
                                    └────────┬──────────┘
                                             │
                        (hora alcanzada)     │      (cancelar manual)
                        [scheduler]          │      [cancelar()]
                                             │
                                    ┌────────▼────────┐
                                    │                 │
                                    │ [EN_JUEGO]      │
                                    │ (En progreso)   │
                                    │                 │
                                    └────────┬────────┘
                                             │
                        (tiempo finalizado)  │
                        [scheduler]          │
                                             │
                                    ┌────────▼──────────────┐
                                    │                       │
                                    │ [FINALIZADO]          │◄─────┐
                                    │ (Terminal)            │      │
                                    │ (Cargar estadísticas) │      │
                                    │                       │      │
                                    └───────────────────────┘      │
                                                                   │
                                          ┌─────────────────────────┘
                                          │
                                    ┌─────▼──────────────┐
                                    │                    │
                                    │ [CANCELADO]        │
                                    │ (Terminal)         │
                                    │ (Procesar sanciones│
                                    │  y reembolsos)     │
                                    │                    │
                                    └────────────────────┘
```

## Matriz de Transiciones

| Estado Actual | Acción | Condición | Nuevo Estado |
|---|---|---|---|
| BUSCANDO | postular | usuario válido | BUSCANDO |
| BUSCANDO | postular | cupo completo | LOBBY_ARMADO |
| BUSCANDO | cancelar | siempre | CANCELADO |
| BUSCANDO | timeout | 24h sin cambios | CANCELADO |
| LOBBY_ARMADO | confirmar | usuario válido | LOBBY_ARMADO |
| LOBBY_ARMADO | confirmar | todos confirmaron | CONFIRMADO |
| LOBBY_ARMADO | cancelar | siempre | CANCELADO |
| LOBBY_ARMADO | timeout | 30min sin confirmaciones | CANCELADO |
| CONFIRMADO | iniciar | manual o automático | EN_JUEGO |
| CONFIRMADO | cancelar | < 5min antes | CANCELADO |
| EN_JUEGO | finalizar | manual | FINALIZADO |
| EN_JUEGO | timeout | duración alcanzada | FINALIZADO |
| FINALIZADO | - | estado terminal | - |
| CANCELADO | - | estado terminal | - |

## Reglas de Negocio por Estado

### BUSCANDO_JUGADORES
- ✅ Nuevos usuarios pueden postularse
- ✅ Organizador puede cancelar
- ✅ Sistema notifica cambios a usuarios interesados
- ❌ No se puede confirmar
- ❌ No se puede iniciar

### LOBBY_ARMADO
- ✅ Usuarios pueden confirmar participación
- ✅ Organizador puede reemplazar un jugador (vuelve a BUSCANDO)
- ✅ Sistema envía reminder de confirmación cada 5 min
- ❌ Nuevos usuarios no pueden postularse
- ❌ No se puede iniciar

### CONFIRMADO
- ✅ Sistema prepara transición a EN_JUEGO
- ✅ Sistema envía recordatorios (1h antes, 15min, 5min)
- ❌ Nuevos usuarios no pueden unirse
- ❌ Usuarios confirmados pueden abandonar (reciben strike)

### EN_JUEGO
- ✅ Sistema registra inicio
- ✅ Sistema bloquea cambios
- ❌ No se acepta sin usuarios abandonan
- ❌ No se pueden hacer cambios

### FINALIZADO
- ✅ Sistema habilita carga de estadísticas
- ✅ Usuarios pueden calificar compañeros
- ✅ Sistema calcula cambios de MMR
- ❌ No se puede volver a otros estados

### CANCELADO
- ✅ Sistema procesa reembolsos (si aplica)
- ✅ Sistema aplica strikes si corresponde
- ✅ Se notifica a todos participantes
- ❌ No se puede reactivar

## Eventos Publicados por Transición

```
BUSCANDO → LOBBY_ARMADO
  ↓
  Evento: ScrimStateChangedEvent(id, "LobbyArmado")
  Listeners: NotificationSubscriber
  Action: Enviar notificación "¡Lobby completado!"

LOBBY_ARMADO → CONFIRMADO
  ↓
  Evento: ScrimStateChangedEvent(id, "Confirmado")
  Listeners: NotificationSubscriber
  Action: Enviar notificación "Todos confirmaron"

CONFIRMADO → EN_JUEGO
  ↓
  Evento: ScrimStateChangedEvent(id, "EnJuego")
  Listeners: NotificationSubscriber
  Action: Enviar notificación "¡Scrim iniciado!"

EN_JUEGO → FINALIZADO
  ↓
  Evento: ScrimStateChangedEvent(id, "Finalizado")
  Listeners: NotificationSubscriber
  Action: Enviar notificación "Scrim finalizado"

* → CANCELADO
  ↓
  Evento: ScrimStateChangedEvent(id, "Cancelado")
  Listeners: NotificationSubscriber
  Action: Enviar notificación "Scrim cancelado"
```

## Implementación

Cada estado es una clase que implementa `ScrimState`:
- `BuscandoJugadoresState`
- `LobbyArmadoState`
- `ConfirmadoState`
- `EnJuegoState`
- `FinalizadoState`
- `CanceladoState`

El contexto (`Scrim`) delega las operaciones al estado actual:

```java
public void postular(Usuario usuario, String rol) {
    estado.postular(this, usuario, rol);
}

public void confirmar(Usuario usuario) {
    estado.confirmar(this, usuario);
}
```

Cada estado implementa estas operaciones de forma diferente según sus reglas.
