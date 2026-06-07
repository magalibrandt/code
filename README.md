# TPO Final - eScrims: Plataforma de Organización de Scrims

## Descripción

Sistema de gestión de scrims (partidas amistosas) para eSports que permite a jugadores crear, buscar y participar en partidas organizadas con emparejamiento inteligente, notificaciones multi-canal y moderación integrada.

### Objetivo del Proyecto

Diseñar una plataforma que facilite:
- ✅ Creación y búsqueda de scrims
- ✅ Emparejamiento inteligente por nivel, latencia e historial
- ✅ Gestión del ciclo de vida del scrim (6 estados)
- ✅ Notificaciones multi-canal (Push, Email, Discord)
- ✅ Sistema de moderación con Chain of Responsibility
- ✅ Comandos reversibles (Command Pattern)
- ✅ Integraciones externas (Adapter Pattern)

## Consistencia de diseño

- `EstadoPostulacion`, `EstadoReporte` y `Sancion` son clases normales del dominio.
- Las comparaciones de estado se hacen con metodos de dominio como `esAceptada()`, `esPendiente()`, `esResuelto()` y `requiereCooldown()`.
- `ScrimController` es un controller simulado sin framework web; no hay API REST real ni Spring Boot.
- `PatternTests` es una clase ejecutable con `main`; no se usa JUnit y los fallos lanzan `AssertionError`.
- Las integraciones externas (`DiscordAdapter`, `SendGridAdapter`, `ICalAdapter`) son simuladas y encapsuladas por Adapter.

## Arquitectura

El proyecto sigue una arquitectura en capas:

```
src/main/java/com/escrims/
├── domain/                      # Capa de Dominio - Lógica pura de negocio
│   ├── model/                  # Entidades y Value Objects
│   │   ├── Usuario.java
│   │   ├── Scrim.java
│   │   ├── Equipo.java
│   │   ├── Postulacion.java
│   │   ├── Confirmacion.java
│   │   ├── Estadistica.java
│   │   ├── ReporteConducta.java
│   │   └── ...
│   ├── state/                  # Patrón State - Ciclo de vida del Scrim
│   │   ├── ScrimState.java (interface)
│   │   ├── BuscandoJugadoresState.java
│   │   ├── LobbyArmadoState.java
│   │   ├── ConfirmadoState.java
│   │   ├── EnJuegoState.java
│   │   ├── FinalizadoState.java
│   │   └── CanceladoState.java
│   ├── strategy/               # Patrón Strategy - Algoritmos de matchmaking
│   │   ├── MatchmakingStrategy.java (interface)
│   │   ├── ByMMRStrategy.java
│   │   ├── ByLatencyStrategy.java
│   │   └── ByHistoryStrategy.java
│   ├── command/                # Patrón Command - Acciones reversibles
│   │   ├── ScrimCommand.java (interface)
│   │   ├── AsignarRolCommand.java
│   │   ├── SwapJugadoresCommand.java
│   │   ├── InvitarJugadorCommand.java
│   │   └── CommandInvoker.java
│   ├── events/                 # Patrón Observer - Sistema de eventos
│   │   ├── DomainEvent.java (interface)
│   │   ├── ScrimStateChangedEvent.java
│   │   ├── PostulacionAceptadaEvent.java
│   │   ├── Subscriber.java (interface)
│   │   ├── DomainEventBus.java (Singleton)
│   │   └── NotificationSubscriber.java
│   └── moderacion/             # Patrón Chain of Responsibility
│       ├── ReportProcessor.java (interface)
│       ├── AutomaticProcessor.java
│       ├── BotProcessor.java
│       └── HumanModeratorProcessor.java
├── application/                # Capa de Aplicación - Casos de uso
│   ├── EscrimsFacade.java      # FACADE PATTERN - Interface unificada
│   ├── ScrimService.java       # Orquestador de lógica
│   ├── ScrimController.java    # Controller simulado, sin framework REST
│   └── builder/                # Patrón Builder
│       └── ScrimBuilder.java
├── infrastructure/             # Capa de Infraestructura - Detalles técnicos
│   ├── notifications/          # Patrón Abstract Factory
│   │   ├── Notifier.java (interface)
│   │   ├── PushNotifier.java
│   │   ├── EmailNotifier.java
│   │   ├── DiscordNotifier.java
│   │   ├── NotifierFactory.java (interface)
│   │   ├── DevNotifierFactory.java
│   │   └── ProdNotifierFactory.java
│   └── adapters/               # Patrón Adapter
│       ├── ExternalServiceAdapter.java
│       ├── DiscordAdapter.java
│       ├── SendGridAdapter.java
│       └── ICalAdapter.java
└── Main.java                   # Punto de entrada

```

## Patrones de Diseño Implementados

### 1. **STATE PATTERN** ⚙️
**Ubicación**: `domain.state`

Gestiona el ciclo de vida del Scrim con 6 estados:
- `BuscandoJugadoresState` - Buscando participantes
- `LobbyArmadoState` - Lobby completado
- `ConfirmadoState` - Todos confirmaron
- `EnJuegoState` - En progreso
- `FinalizadoState` - Terminado
- `CanceladoState` - Cancelado

**Beneficio**: Elimina condicionales complejos, cada estado maneja su propia lógica.

---

### 2. **STRATEGY PATTERN** 🎯
**Ubicación**: `domain.strategy`

Algoritmos de emparejamiento intercambiables:
- `ByMMRStrategy` - Por rango/nivel
- `ByLatencyStrategy` - Por ping
- `ByHistoryStrategy` - Por comportamiento/compatibilidad

**Beneficio**: Cambiar algoritmos en runtime sin modificar código.

---

### 3. **OBSERVER PATTERN** 👁️
**Ubicación**: `domain.events`

Sistema de eventos desacoplado:
- `DomainEventBus` - Subject/Singleton
- `Subscriber` - Observer interface
- `NotificationSubscriber` - Observer concreto
- `ScrimStateChangedEvent`, `PostulacionAceptadaEvent`

**Beneficio**: Dominio desacoplado de notificaciones.

---

### 4. **FACADE PATTERN** 🏛️ ⭐ REQUERIDO
**Ubicación**: `application`

Interfaz simplificada al sistema completo:
- `EscrimsFacade` - Punto de entrada único
- Encapsula ScrimService, EventBus, Commands, Adapters, Factories
- Métodos simples para todas las operaciones
- Desacopla cliente de complejidad interna

**Beneficio**: Interfaz clara y unificada, bajo acoplamiento.

---

### 5. **ABSTRACT FACTORY PATTERN** 🏭
**Ubicación**: `infrastructure.notifications`

Creación de notificadores por entorno:
- `NotifierFactory` - Abstract Factory
- `DevNotifierFactory` - Mocks para desarrollo
- `ProdNotifierFactory` - Implementaciones reales
- `PushNotifier`, `EmailNotifier`, `DiscordNotifier`

**Beneficio**: Cambiar entorno (dev/prod) sin modificar aplicación.

---

### 6. **COMMAND PATTERN** 📝
**Ubicación**: `domain.command`

Acciones reversibles antes de confirmar:
- `ScrimCommand` - Command interface
- `AsignarRolCommand` - Asignar rol a usuario
- `SwapJugadoresCommand` - Intercambiar jugadores
- `InvitarJugadorCommand` - Invitar jugador
- `CommandInvoker` - Executor con undo/redo

**Beneficio**: Operaciones deshacibles, historial de acciones.

---

### 7. **CHAIN OF RESPONSIBILITY PATTERN** ⛓️
**Ubicación**: `domain.moderacion`

Procesamiento escalonado de reportes:
- `ReportProcessor` - Handler interface
- `AutomaticProcessor` - Resuelve automáticamente
- `BotProcessor` - Análisis algorítmico
- `HumanModeratorProcessor` - Decisión humana

**Beneficio**: Escalación flexible de sanciones, sin acoplar niveles.

---

### 8. **ADAPTER PATTERN** 🔌
**Ubicación**: `infrastructure.adapters`

Integración con servicios externos:
- `ExternalServiceAdapter` - Adapter interface
- `DiscordAdapter` - Discord webhooks/bots
- `SendGridAdapter` - Email (SMTP)
- `ICalAdapter` - Sincronización de calendarios

**Beneficio**: Desacoplar integraciones, cambiar proveedores fácilmente.

---

### 9. **BUILDER PATTERN** 🔨
**Ubicación**: `application.builder`

Construcción validada de Scrims:
- `ScrimBuilder` - Constructor fluido
- Validaciones en cada paso
- `build()` verifica invariantes

**Beneficio**: Construcción segura, código legible.

---

## Principios SOLID

✅ **SRP** (Single Responsibility): Cada clase tiene una razón para cambiar  
✅ **OCP** (Open/Closed): Extensible sin modificar  
✅ **LSP** (Liskov Substitution): Subtipos sustituibles  
✅ **ISP** (Interface Segregation): Interfaces pequeñas  
✅ **DIP** (Dependency Inversion): Depender de abstracciones

## Principios GRASP

✅ **Expert**: Responsabilidades por conocimiento  
✅ **Creator**: Creación delegada  
✅ **Controller**: Service orquesta casos de uso  
✅ **Low Coupling**: Bajo acoplamiento entre capas  
✅ **High Cohesion**: Alta cohesión interno  
✅ **Polymorphism**: Uso extensivo de polimorfismo  
✅ **Protected Variations**: Protección contra cambios  
✅ **Indirection**: Intermediarios para desacoplar  
✅ **Pure Fabrication**: Clases auxiliares útiles

## Compilación y Ejecución

### ⚠️ IMPORTANTE: Solución al Error de Compilación

Si ves errores como `NotifierFactory cannot be resolved to a type`, es porque la estructura de compilación es incorrecta.

### Opción 1: Scripts Automáticos (RECOMENDADO)

#### Linux/Mac:
```bash
chmod +x compile.sh run.sh
./compile.sh
./run.sh
```

#### Windows:
```cmd
compile.bat
run.bat
```

### Opción 2: Compilación Manual

#### Compilar:
```bash
javac -d out -sourcepath src/main/java \
  src/main/java/com/escrims/**/*.java \
  src/main/java/com/escrims/*.java
```

#### Ejecutar:
```bash
java -cp out com.escrims.Main
```

### Opción 3: IntelliJ IDEA

1. File > Open > Seleccionar carpeta del proyecto
2. Marcar `src/main/java` como Source
3. Right-click en `Main.java` > Run

## Controller simulado

### Autenticación
```http
POST /api/auth/register
POST /api/auth/login
```

### Búsqueda
```http
GET /api/scrims?juego=Valorant&region=LATAM&rangoMin=Gold&rangoMax=Platinum
```

### Gestión de Scrims
```http
POST /api/scrims                           # Crear
GET /api/scrims/{id}                       # Obtener detalles
POST /api/scrims/{id}/postulaciones        # Postularse
POST /api/scrims/{id}/confirmaciones       # Confirmar
POST /api/scrims/{id}/iniciar              # Iniciar
POST /api/scrims/{id}/finalizar            # Finalizar
POST /api/scrims/{id}/estadisticas         # Cargar stats
POST /api/scrims/{id}/cancelar             # Cancelar
```

### Comandos
```http
POST /api/scrims/{id}/acciones/asignar-rol
POST /api/scrims/{id}/acciones/swap-jugadores
POST /api/scrims/{id}/acciones/invitar-jugador
```

## Casos de Uso

Ver [CASOS_DE_USO.md](CASOS_DE_USO.md) para:
- CU1: Registrar Usuario
- CU2: Autenticar Usuario
- CU3: Crear Scrim
- CU4: Postularse a Scrim
- CU5: Emparejar y Armar Lobby
- CU6: Confirmar Participación
- CU7: Iniciar Scrim
- CU8: Finalizar y Cargar Estadísticas
- CU9: Cancelar Scrim
- CU10: Enviar Notificaciones
- CU11: Moderar Reportes

## Diagrama de Estados

Ver [DIAGRAMA_ESTADOS.md](DIAGRAMA_ESTADOS.md) para:
- Descripción de 6 estados
- Transiciones y condiciones
- Matriz de transiciones
- Eventos publicados
- Reglas de negocio por estado

## Ejemplos de Uso

```java
// 1. Crear usuario
Usuario usuario = new Usuario("ProPlayer", "email@test.com", "hash123", "LATAM");
usuario.agregarRango("Valorant", "Gold");

// 2. Crear servicio con estrategia
MatchmakingStrategy strategy = new ByMMRStrategy();
ScrimService service = new ScrimService(strategy);

// 3. Crear scrim con Builder
Scrim scrim = new ScrimBuilder("Valorant", "5v5", "LATAM", usuario)
    .conRangos("Gold", "Platinum")
    .conLatenciaMaxima(80)
    .conFechaHora(LocalDateTime.now().plusHours(2))
    .conCupos(10)
    .build();

// 4. Postularse
scrim.postular(usuario2, "Duelist");

// 5. Cambiar estrategia en runtime
service.setMatchmakingStrategy(new ByLatencyStrategy());

// 6. Procesar reporte con Chain of Responsibility
ReporteConducta reporte = new ReporteConducta(scrim, reporter, reportado, "SPAM", "...");
service.procesarReporte(reporte);
```

## Testing

### Unit Tests
- `ByMMRStrategyTest` - Validar algoritmo de matchmaking
- `ScrimStateTransitionsTest` - Validar transiciones de estado
- `NotifierFactoryTest` - Validar creación de notificadores
- `CommandPatternTest` - Validar comandos y undo

### Integration Tests
- Flujo completo: crear → postular → armar → confirmar → iniciar → finalizar
- Moderación de reportes con chain of responsibility
- Cambio de estrategias en runtime

### E2E Tests
- Flujo de cliente simulado mediante llamadas Java a controller/facade
- Eventos publicados y suscritos
- Notificaciones multi-canal

## Estructura de Datos

### Usuario
```java
UUID id
String username
String email
String passwordHash
Map<String, String> rangoPorJuego      // "Valorant" → "Gold"
List<String> rolesPreferidos            // "Duelist", "Support"
String region
int strikes
Date cooldownHasta
```

### Scrim
```java
UUID id
String juego
String formato                           // "5v5", "3v3", "1v1"
String region
String rangoMin, rangoMax
int latenciaMax
LocalDateTime fechaHora
int cuposTotales
Usuario creador
ScrimState estado                       // Patrón State
List<Postulacion> postulaciones
List<Confirmacion> confirmaciones
Equipo equipoA, equipoB
Estadistica estadistica
```

## Archivos de Configuración

- `compile.sh` / `compile.bat` - Scripts de compilación
- `run.sh` / `run.bat` - Scripts de ejecución
- `package.json` - Metadatos del proyecto
- `DIAGRAMA_CLASES.md` - Diagrama UML completo
- `JUSTIFICACION_PATRONES.md` - Justificación de cada patrón
- `CASOS_DE_USO.md` - Casos de uso detallados
- `DIAGRAMA_ESTADOS.md` - Máquina de estados
- `INSTRUCCIONES_COMPILACION.md` - Guía de compilación

## Extensiones Futuras (Bonus)

- [ ] Matchmaking híbrido (ponderar MMR + latencia + historial)
- [ ] Rank decay y recálculo de MMR
- [ ] Colas de notificaciones (RabbitMQ/Kafka)
- [ ] Sistema de reputación con anti-smurfing
- [ ] Base de datos (JPA/Hibernate)
- [ ] Autenticación OAuth (Steam, Discord, Riot)
- [ ] App móvil (React Native/Flutter)
- [ ] Sincronización iCal con Google Calendar

## Notas Importantes

### Patrones Mínimos Requeridos
✅ **State Pattern** - Gestión del ciclo de vida  
✅ **Strategy Pattern** - Algoritmos intercambiables  
✅ **Observer Pattern** - Sistema de eventos  
✅ **Abstract Factory** - Notificadores por entorno

### Patrones Adicionales Implementados
✅ **Command Pattern** - Acciones reversibles  
✅ **Chain of Responsibility** - Moderación escalonada  
✅ **Adapter Pattern** - Integraciones externas  
✅ **Builder Pattern** - Construcción validada

### Principios Aplicados
✅ **SOLID** - Todos los principios
✅ **GRASP** - Todos aplicados
✅ **MVC** - Separación de capas
✅ **Desacoplamiento** - Observer, Factory, Adapter

---

**Última actualización**: Junio 2026

\`\`\`

#### Windows:
\`\`\`cmd
compile.bat
run.bat
\`\`\`

### Opción 2: Compilación Manual

**IMPORTANTE**: Debes estar en la raíz del proyecto (donde está este README)

\`\`\`bash
# Eliminar compilaciones anteriores incorrectas
rm -rf bin/ out/

# Compilar correctamente
javac -d out -sourcepath src/main/java src/main/java/com/escrims/**/*.java src/main/java/com/escrims/*.java

# Ejecutar
java -cp out com.escrims.Main
\`\`\`

### Opción 3: Usar un IDE

#### IntelliJ IDEA:
1. File → Open → Selecciona la carpeta del proyecto
2. Click derecho en `src/main/java` → Mark Directory as → Sources Root
3. Click derecho en `Main.java` → Run 'Main.main()'

#### Eclipse:
1. File → Import → Existing Projects into Workspace
2. Selecciona la carpeta del proyecto
3. Click derecho en el proyecto → Properties → Java Build Path
4. En "Source", asegúrate que sea `src/main/java`
5. Click derecho en `Main.java` → Run As → Java Application

#### VS Code:
1. Instala "Extension Pack for Java"
2. Abre la carpeta del proyecto
3. Presiona F5 o click en "Run" en `Main.java`

## Salida Esperada

El programa demuestra:
1. Configuración de notificaciones (Abstract Factory)
2. Sistema de eventos (Observer)
3. Creación de usuarios
4. Creación de scrim (Builder)
5. Ciclo de vida completo (State)
6. Cambio de estrategias (Strategy)

\`\`\`
=== eScrims: Sistema de Organización de Scrims ===

1. Configurando sistema de notificaciones...
2. Configurando sistema de eventos...
...
=== RESUMEN DE PATRONES DEMOSTRADOS ===
✓ STATE: Ciclo de vida del Scrim (6 estados)
✓ STRATEGY: 3 algoritmos de emparejamiento intercambiables
✓ OBSERVER: Sistema de eventos y notificaciones
✓ ABSTRACT FACTORY: Creación de notificadores por entorno
✓ BUILDER: Construcción validada de Scrims

=== Sistema funcionando correctamente ===
\`\`\`

## Casos de Uso Implementados

- CU1: Registrar usuario
- CU2: Buscar scrims
- CU3: Crear scrim
- CU4: Postularse a scrim
- CU5: Emparejar jugadores
- CU6: Confirmar participación
- CU7: Iniciar scrim
- CU8: Finalizar scrim
- CU9: Cancelar scrim
- CU10: Notificar eventos

## Documentación Adicional

- `JUSTIFICACION_PATRONES.md`: Justificación detallada de cada patrón
- `DIAGRAMA_CLASES.md`: Diagrama de clases UML en formato texto
- `INSTRUCCIONES_COMPILACION.md`: Guía completa de compilación y troubleshooting

## Requisitos

- Java 11 o superior
- No requiere dependencias externas

## Troubleshooting

Ver `INSTRUCCIONES_COMPILACION.md` para soluciones detalladas a problemas comunes.

## Integrantes

Agustín Arguello - LU 1167126
Taiel Vinograd - LU 1167839
Melinda Selles - LU 1124972
Germán Schettini - LU 1163057
Magali Brandt - LU 1167149
## Fecha de Entrega

18/06/2026
