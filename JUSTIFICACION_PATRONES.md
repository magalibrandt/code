# Justificación de Patrones de Diseño - eScrims

## Nota de consistencia con el UML actualizado

`EstadoPostulacion`, `EstadoReporte` y `Sancion` son clases con atributos y comportamiento propio, alineadas con el diagrama de clases.

El patron Command opera sobre el lobby armado: `AsignarRolCommand` modifica el rol asignado en `Equipo`, `SwapJugadoresCommand` intercambia jugadores entre `Equipo A` y `Equipo B`, e `InvitarJugadorCommand` registra una postulacion mediante el flujo de dominio.

## 1. PATRÓN STATE

### Problema que resuelve
El ciclo de vida de un Scrim tiene múltiples estados (Buscando Jugadores, Lobby Armado, Confirmado, En Juego, Finalizado, Cancelado) y cada estado tiene comportamientos diferentes para las mismas operaciones (postular, confirmar, iniciar, finalizar, cancelar).

### Solución implementada
- **Contexto**: `Scrim` - mantiene referencia al estado actual
- **Interface State**: `ScrimState` - define operaciones comunes
- **Estados Concretos**: 
  - `BuscandoJugadoresState`
  - `LobbyArmadoState`
  - `ConfirmadoState`
  - `EnJuegoState`
  - `FinalizadoState`
  - `CanceladoState`

### Beneficios
- **Open/Closed Principle (SOLID)**: Podemos agregar nuevos estados sin modificar código existente
- **Single Responsibility (SOLID)**: Cada estado maneja su propia lógica
- **Elimina condicionales**: No hay if/switch gigantes para manejar estados
- **Transiciones claras**: Cada estado conoce sus transiciones válidas

### Aplicación de GRASP
- **Polimorfismo**: Las operaciones varían según el estado concreto
- **Protected Variations**: El contexto está protegido de cambios en la lógica de estados

---

## 2. PATRÓN STRATEGY

### Problema que resuelve
El sistema necesita diferentes algoritmos de emparejamiento (por MMR, por latencia, por historial) y debe poder cambiarlos en tiempo de ejecución según las necesidades del scrim o preferencias del organizador.

### Solución implementada
- **Interface Strategy**: `MatchmakingStrategy`
- **Estrategias Concretas**:
  - `ByMMRStrategy` - empareja por rango/nivel
  - `ByLatencyStrategy` - empareja por ping/región
  - `ByHistoryStrategy` - empareja por comportamiento previo

### Beneficios
- **Open/Closed Principle (SOLID)**: Nuevos algoritmos sin modificar código existente
- **Dependency Inversion (SOLID)**: `ScrimService` depende de abstracción, no de implementaciones concretas
- **Flexibilidad**: Cambio de algoritmo en runtime
- **Testeable**: Cada estrategia se puede probar independientemente

### Aplicación de GRASP
- **Polimorfismo**: Diferentes algoritmos con misma interfaz
- **Expert**: Cada estrategia es experta en su criterio de selección
- **Low Coupling**: ScrimService no conoce detalles de implementación

---

## 3. PATRÓN OBSERVER

### Problema que resuelve
Múltiples componentes del sistema necesitan reaccionar a eventos de dominio (cambios de estado, postulaciones aceptadas) sin crear acoplamiento directo entre el dominio y la infraestructura de notificaciones.

### Solución implementada
- **Subject**: `DomainEventBus` (Singleton)
- **Observer Interface**: `Subscriber`
- **Observers Concretos**: `NotificationSubscriber`
- **Eventos**: `DomainEvent`, `ScrimStateChangedEvent`, `PostulacionAceptadaEvent`

### Beneficios
- **Open/Closed Principle (SOLID)**: Nuevos suscriptores sin modificar publicadores
- **Single Responsibility (SOLID)**: Separación entre lógica de negocio y notificaciones
- **Desacoplamiento**: El dominio no conoce la infraestructura
- **Extensibilidad**: Fácil agregar nuevos tipos de eventos y suscriptores

### Aplicación de GRASP
- **Low Coupling**: Dominio desacoplado de notificaciones
- **Indirection**: EventBus actúa como intermediario
- **Protected Variations**: Cambios en notificaciones no afectan dominio

---

## 4. PATRÓN ABSTRACT FACTORY

### Problema que resuelve
El sistema necesita crear familias de objetos relacionados (notificadores Push, Email, Discord) que varían según el entorno (desarrollo vs producción) sin especificar sus clases concretas.

### Solución implementada
- **Abstract Factory**: `NotifierFactory`
- **Factories Concretas**:
  - `DevNotifierFactory` - crea notificadores mock para desarrollo
  - `ProdNotifierFactory` - crea notificadores reales para producción
- **Productos**: `PushNotifier`, `EmailNotifier`, `DiscordNotifier`

### Beneficios
- **Dependency Inversion (SOLID)**: Código depende de abstracciones
- **Consistencia**: Garantiza que se usen notificadores del mismo entorno
- **Flexibilidad**: Cambio de entorno sin modificar código cliente
- **Testeable**: Fácil usar mocks en tests

### Aplicación de GRASP
- **Creator**: Factory decide qué objetos crear
- **Pure Fabrication**: Factory es una clase artificial que no representa concepto de dominio
- **Low Coupling**: Clientes no conocen clases concretas

---

## 5. PATRÓN FACADE 

### Problema que resuelve
El sistema tiene múltiples subsistemas complejos (ScrimService, EventBus, CommandInvoker, Adapters, etc.) que el cliente debe conocer y coordinar. Esto crea acoplamiento alto y código complicado en el cliente.

### Solución implementada
- **Facade**: `EscrimsFacade` - Interfaz unificada y simplificada
- **Subsistemas Encapsulados**:
  - ScrimService (gestión de scrims)
  - DomainEventBus (eventos)
  - CommandInvoker (operaciones reversibles)
  - NotificationSubscriber (notificaciones)
  - ReportProcessor (moderación)
  - Adapters (Discord, SendGrid, iCal)
  - Notifier Factory (notificadores)

### Métodos Proporcionados por el Facade
```java
// Usuario
crearUsuario()
obtenerInfoUsuario()

// Scrims
crearScrim()
buscarScrims()
postularseAScrim()
confirmarParticipacion()
iniciarScrim()
finalizarScrim()
cancelarScrim()

// Operaciones reversibles
asignarRol()
intercambiarJugadores()
invitarJugador()

// Moderación
reportarConductaInapropiada()
obtenerSanciones()

// Integraciones
conectarDiscord(), enviarMensajeDiscord()
conectarSendGrid(), enviarEmail()
conectarICal(), exportarEventoCalendario()

// Estrategias
cambiarEstrategiaMatchmaking()
emparejarJugadores()

// Utilidades
obtenerScrim(), obtenerUsuario()
listarTodosScrims(), listarTodosUsuarios()
```

### Beneficios
- **Interfaz Simplificada**: Cliente solo conoce Facade, no subsistemas
- **Bajo Acoplamiento (SOLID DIP)**: Cliente depende de Facade, no de implementaciones
- **Open/Closed (SOLID OCP)**: Cambios en subsistemas sin afectar cliente
- **Mantenibilidad**: Cambios internos sin afectar interfaz pública
- **Usabilidad**: API clara y fácil de usar

### Aplicación de GRASP
- **Controller**: EscrimsFacade coordina casos de uso
- **Facade**: Proporciona interfaz unificada
- **Low Coupling**: Cliente desacoplado de complejidad
- **Pure Fabrication**: Clase artificial para mejorar diseño

---

## 6. PATRÓN BUILDER (Opcional)

### Problema que resuelve
La creación de un Scrim requiere muchos parámetros opcionales y validaciones complejas. Un constructor con muchos parámetros es difícil de usar y mantener.

### Solución implementada
- **Builder**: `ScrimBuilder`
- **Producto**: `Scrim`
- **Métodos encadenados**: `conRangos()`, `conLatenciaMaxima()`, `conFechaHora()`, etc.
- **Validaciones**: En cada paso y al final en `build()`

### Beneficios
- **Single Responsibility (SOLID)**: Separación entre construcción y representación
- **Legibilidad**: Código más expresivo y fácil de entender
- **Validación**: Garantiza objetos válidos
- **Flexibilidad**: Construcción paso a paso con validaciones

### Aplicación de GRASP
- **Creator**: Builder es responsable de crear Scrims complejos
- **Expert**: Builder conoce las reglas de construcción
- **Low Coupling**: Clientes no conocen detalles de construcción

---

## Principios SOLID Aplicados

### Single Responsibility Principle (SRP)
- Cada clase tiene una única razón para cambiar
- `Scrim` maneja datos, delega comportamiento a `ScrimState`
- `ScrimService` orquesta casos de uso
- `NotificationSubscriber` solo maneja notificaciones

### Open/Closed Principle (OCP)
- Nuevos estados sin modificar `Scrim`
- Nuevas estrategias sin modificar `ScrimService`
- Nuevos suscriptores sin modificar `DomainEventBus`

### Liskov Substitution Principle (LSP)
- Cualquier `ScrimState` puede sustituir a otro
- Cualquier `MatchmakingStrategy` puede sustituir a otra
- Cualquier `Notifier` puede sustituir a otro

### Interface Segregation Principle (ISP)
- Interfaces pequeñas y específicas
- `ScrimState` solo define operaciones de estado
- `MatchmakingStrategy` solo define selección
- `Subscriber` solo define reacción a eventos

### Dependency Inversion Principle (DIP)
- `ScrimService` depende de `MatchmakingStrategy` (abstracción)
- `NotificationSubscriber` depende de `NotifierFactory` (abstracción)
- Dominio no depende de infraestructura

---

## Principios GRASP Aplicados

### Expert
- `Scrim` es experto en su propio estado
- Cada `Strategy` es experta en su criterio
- `Usuario` es experto en sus preferencias

### Creator
- `ScrimService` crea `Scrim` (tiene los datos)
- `ScrimBuilder` crea `Scrim` complejos
- `NotifierFactory` crea `Notifier`

### Controller
- `ScrimService` coordina casos de uso
- No es un controller de UI, sino de aplicación

### Low Coupling
- Dominio desacoplado de infraestructura vía Observer
- Clientes desacoplados de implementaciones vía interfaces

### High Cohesion
- Cada clase tiene responsabilidades relacionadas
- Estados cohesivos en su lógica
- Estrategias cohesivas en su algoritmo

### Polymorphism
- Estados polimórficos
- Estrategias polimórficas
- Notificadores polimórficos

### Protected Variations
- `Scrim` protegido de cambios en lógica de estados
- `ScrimService` protegido de cambios en algoritmos
- Dominio protegido de cambios en notificaciones

### Indirection
- `DomainEventBus` como intermediario
- `NotifierFactory` como intermediario
- `ScrimService` como intermediario

### Pure Fabrication
- `DomainEventBus` no es concepto de dominio
- `NotifierFactory` no es concepto de dominio
- Son clases artificiales para mejorar diseño

---

## 6. PATRÓN COMMAND

### Problema que resuelve
El organizador necesita poder hacer cambios al scrim antes de confirmarlo (asignar roles, intercambiar jugadores, invitar gente específica) y poder deshacer esos cambios si se arrepiente.

### Solución implementada
- **Command Interface**: `ScrimCommand`
- **Comandos Concretos**:
  - `AsignarRolCommand` - Asignar rol a usuario
  - `SwapJugadoresCommand` - Intercambiar dos jugadores
  - `InvitarJugadorCommand` - Invitar jugador específico
- **Invoker**: `CommandInvoker` - Ejecuta, deshace y rehace

### Beneficios
- **Undo/Redo**: Historial de acciones reversibles
- **Open/Closed Principle (SOLID)**: Nuevos comandos sin modificar existentes
- **Separación**: Acción separada de su ejecución
- **Auditoria**: Registro de todas las operaciones

### Aplicación de GRASP
- **Command**: Encapsula acciones como objetos
- **Expert**: CommandInvoker es experto en gestionar historial
- **Protected Variations**: Cambios de scrim aislados en comandos

---

## 7. PATRÓN CHAIN OF RESPONSIBILITY

### Problema que resuelve
Los reportes de conducta necesitan procesamiento escalonado: primero automático, luego bot, finalmente humano. Cada nivel decide si puede resolver o escala al siguiente, sin que el cliente conozca la cadena.

### Solución implementada
- **Handler Interface**: `ReportProcessor`
- **Handlers Concretos**:
  - `AutomaticProcessor` - Resuelve casos claros (spam, no-show)
  - `BotProcessor` - Análisis algorítmico (historial, patrones)
  - `HumanModeratorProcessor` - Decisión final (revisión completa)
- **Cadena**: Cada procesador conoce el siguiente
- **Modelo de Dominio**: `ReporteConducta` con estados y sanciones

### Beneficios
- **Open/Closed Principle (SOLID)**: Nuevos niveles sin modificar existentes
- **Single Responsibility (SOLID)**: Cada procesador tiene responsabilidad clara
- **Escalabilidad**: Fácil agregar niveles (ej: especialista por juego)
- **Flexibilidad**: Dinámicamente construir cadenas diferentes

### Aplicación de GRASP
- **Responsibility**: Cada procesador responsable de su nivel
- **Low Coupling**: Procesadores no dependen entre sí
- **Polymorphism**: Todos cumplen la misma interface
- **Indirection**: Cadena oculta del cliente

---

## 8. PATRÓN ADAPTER (Adicional)

### Problema que resuelve
Integrar servicios externos (Discord, SendGrid, iCal) con interfaces diferentes. El dominio no debe conocer detalles de cómo se conectan.

### Solución implementada
- **Adapter Interface**: `ExternalServiceAdapter`
- **Adaptadores Concretos**:
  - `DiscordAdapter` - Webhooks y bots
  - `SendGridAdapter` - Envío de emails
  - `ICalAdapter` - Sincronización de calendarios

### Beneficios
- **Dependency Inversion (SOLID)**: Depender de abstracción
- **Flexibilidad**: Cambiar proveedores sin cambiar código
- **Testeable**: Mockear adaptadores en tests
- **Separación**: Lógica de negocio de detalles técnicos

### Aplicación de GRASP
- **Adapter**: Convierte interfaces incompatibles
- **Creator**: NotifierFactory crea adaptadores
- **Low Coupling**: Aplicación desacoplada de servicios

---

## Resumen de Patrones

| Patrón | Ubicación | Propósito | Beneficio Clave |
|--------|-----------|----------|---|
| **State** | domain.state | Ciclo de vida del Scrim | Elimina condicionales |
| **Strategy** | domain.strategy | Algoritmos de matchmaking | Intercambiables en runtime |
| **Observer** | domain.events | Sistema de eventos | Desacoplamiento dominio-infra |
| **Facade** | application | Interface unificada | Simplifica acceso al sistema |
| **Command** | domain.command | Acciones reversibles | Undo/Redo y auditoría |
| **Chain of Responsibility** | domain.moderacion | Procesamiento escalonado | Escalación flexible |
| **Adapter** | infrastructure.adapters | Integraciones externas | Cambiar proveedores |
| **Abstract Factory** | infrastructure.notifications | Crear notificadores | Dev vs Prod sin cambios |
| **Builder** | application.builder | Construcción validada | Construcción segura |

---

## Conclusión

El diseño implementado cumple con **todos los requisitos de patrones**:

### Patrones Mínimos Requeridos (✅ TODOS IMPLEMENTADOS)
- ✅ **STATE PATTERN** - Ciclo de vida del Scrim (6 estados)
- ✅ **STRATEGY PATTERN** - 3 algoritmos de matchmaking intercambiables
- ✅ **OBSERVER PATTERN** - Sistema de eventos desacoplado
- ✅ **FACADE PATTERN** - Interface unificada al sistema

### Patrones Adicionales Implementados
- ✅ **COMMAND PATTERN** - Acciones reversibles con undo/redo
- ✅ **CHAIN OF RESPONSIBILITY** - Moderación escalonada
- ✅ **ADAPTER PATTERN** - Integraciones externas
- ✅ **ABSTRACT FACTORY PATTERN** - Notificadores por entorno
- ✅ **BUILDER PATTERN** - Construcción validada

### Principios SOLID Aplicados
- ✅ **SRP** (Single Responsibility Principle)
- ✅ **OCP** (Open/Closed Principle)
- ✅ **LSP** (Liskov Substitution Principle)
- ✅ **ISP** (Interface Segregation Principle)
- ✅ **DIP** (Dependency Inversion Principle)

### Principios GRASP Aplicados
- ✅ Todos 9 principios GRASP
- ✅ Low Coupling & High Cohesion
- ✅ Polymorphism & Protected Variations
- ✅ Indirection & Pure Fabrication

### Conclusión Final
✅ **4/4 patrones mínimos requeridos** (State, Strategy, Observer, Facade)  
✅ **5 patrones adicionales** para robustez y extensibilidad  
✅ **Arquitectura MVC** con dominio separado  
✅ **Código de producción** escalable y mantenible  
✅ **Documentación exhaustiva**

