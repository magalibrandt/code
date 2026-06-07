# Resumen de pautas de evaluación - ADOO / Proceso de Desarrollo de Software

> Archivo base: `adoo_Modos de evaluacion Examenes.pdf`  
> Uso sugerido dentro del repo: `docs/evaluacion/modos-evaluacion-resumen.md`

## 1. Alcance del examen / trabajo

El examen o trabajo integrador evalúa la aplicación correcta de los temas vistos durante la cursada de Análisis y Diseño Orientado a Objetos.

Los ejes principales de evaluación son:

- Principios **GRASP**.
- Principios **SOLID**.
- **Diagrama de clases** y sus relaciones.
- **Patrones de diseño**, incluyendo su aplicación y justificación.

## 2. Condición de aprobación

Para aprobar se necesita tener aproximadamente el **70% del examen bien realizado**.

La parte más crítica es el **diagrama de clases**:

- Si el diagrama de clases es erróneo o incompleto, no se puede aprobar.
- La justificación de patrones solo se considera si la parte práctica del diagrama de clases está aprobada.
- Si el diagrama de clases está correcto pero no existe justificación del uso de patrones, tampoco se aprueba.

Conclusión operativa:

> El diagrama de clases, el código y la justificación de patrones deben estar alineados. No alcanza con que el código funcione: el diseño tiene que ser correcto y justificable.

## 3. Distribución de puntaje

| Parte evaluada | Puntaje |
|---|---:|
| Diagrama de clases | 6 puntos |
| Justificación del uso de patrones | 4 puntos |

## 4. Penalizaciones indicadas

| Error cometido | Puntos que resta |
|---|---:|
| Clases vacías | -2.5 |
| Incorrecta aplicación de un patrón de diseño | -2 |
| Incorrecta aplicación de GRASP | -1.5 |
| Violación a los principios SOLID | -1.5 |
| Atributos de una clase públicos | -1 |
| Relación incorrecta | -0.5 |
| No colocar la frase verbal de la relación | -0.5 |
| No colocar la cardinalidad de una relación | -0.5 |
| Métodos sin parámetros cuando los necesitan | -0.5 |
| Métodos sin el tipo que devuelven | -0.5 |

## 5. Reglas prácticas para el diagrama de clases

Para evitar penalizaciones, el diagrama debe cumplir estas reglas:

### 5.1 Clases

- No debe haber clases vacías.
- Cada clase relevante debe tener atributos y métodos.
- Los atributos deben ser privados.
- Las clases deben tener responsabilidades coherentes.
- No conviene crear clases decorativas que no aporten comportamiento ni datos relevantes.

### 5.2 Métodos

- Los métodos deben indicar tipo de retorno.
- Los métodos deben incluir parámetros cuando sean necesarios.
- No alcanza con poner solo el nombre del método si el método requiere datos para operar.

Ejemplo aceptable:

```text
+ confirmarParticipacion(scrimId: UUID, usuarioId: UUID): void
```

Ejemplo incompleto:

```text
+ confirmarParticipacion()
```

### 5.3 Relaciones

- Las relaciones deben estar correctamente elegidas.
- Deben tener cardinalidad.
- Deben tener frase verbal cuando corresponda.
- La relación del diagrama debe poder verse reflejada en el código mediante atributos, dependencias o uso real.

Ejemplo aceptable:

```text
Scrim "1" *-- "0..*" Postulacion : contiene
```

### 5.4 Interfaces y clases abstractas

- Las interfaces deben estar claramente marcadas.
- Las clases abstractas deben estar claramente marcadas.
- Las implementaciones deben estar conectadas mediante relaciones correctas.

Ejemplo:

```text
interface MatchmakingStrategy <<interface>>
ByMMRStrategy ..|> MatchmakingStrategy
```

## 6. GRASP: criterios a cuidar

El PDF marca como error la incorrecta aplicación de GRASP. Para este trabajo, revisar especialmente:

### 6.1 Experto en Información

La responsabilidad debe estar en la clase que tiene la información necesaria.

Ejemplos aplicados al proyecto:

- `EstadisticaJugador` calcula su KDA porque conoce kills, deaths y assists.
- `Scrim` maneja postulaciones, confirmaciones y estado porque concentra la información del scrim.

### 6.2 Bajo Acoplamiento

Evitar que una clase dependa innecesariamente de demasiadas clases concretas.

Ejemplos a revisar:

- `ScrimService` debería depender de `MatchmakingStrategy`, no de una estrategia concreta.
- `NotificationSubscriber` debería depender de `NotifierFactory` y `Notifier`, no de todos los notificadores concretos directamente.

### 6.3 Alta Cohesión

Cada clase debe tener una responsabilidad principal clara.

Evitar clases que hagan demasiadas cosas, por ejemplo:

- validar usuarios;
- crear scrims;
- ejecutar matchmaking;
- enviar emails;
- procesar reportes;
- persistir datos;
- renderizar respuestas.

Si una clase concentra todo eso, puede convertirse en una God Class.

### 6.4 Controlador

El controller debe recibir solicitudes y delegar.

No debería tener lógica fuerte de negocio.

Estructura esperada:

```text
ScrimController -> ScrimService -> Dominio
```

## 7. SOLID: criterios a cuidar

El PDF marca como error la violación a principios SOLID. Para este proyecto, revisar especialmente:

### 7.1 SRP - Single Responsibility Principle

Cada clase debe tener una sola razón principal de cambio.

Ejemplo de riesgo:

```text
ScrimService crea scrims, maneja usuarios, ejecuta matchmaking, procesa reportes, envía notificaciones y administra persistencia.
```

No siempre es necesario separar todo en un TP, pero sí debe evitarse una clase excesivamente cargada.

### 7.2 OCP - Open/Closed Principle

El diseño debe permitir agregar variantes sin modificar código existente.

Ejemplo aplicado:

- Para agregar una nueva estrategia de matchmaking, se crea una nueva clase que implemente `MatchmakingStrategy`.
- No debería ser necesario modificar un `switch` gigante dentro de `ScrimService`.

### 7.3 DIP - Dependency Inversion Principle

Las clases de alto nivel deberían depender de abstracciones.

Ejemplos aplicados:

- `ScrimService` depende de `MatchmakingStrategy`.
- `NotificationSubscriber` depende de `NotifierFactory`.
- `CommandInvoker` depende de `ScrimCommand`.

## 8. Patrones de diseño

El PDF indica que la aplicación incorrecta de un patrón resta mucho puntaje. Además, la justificación del patrón es obligatoria para aprobar.

Cada patrón debe poder explicarse con:

1. Problema que resuelve.
2. Clases que participan.
3. Cómo se aplica en el código.
4. Beneficio de diseño.
5. Relación con GRASP/SOLID si corresponde.

## 9. Advertencias específicas sobre State

El PDF incluye ejemplos de errores en la aplicación del patrón State. Para evitar esos errores:

- El método de cambio de estado no debe estar mal ubicado en el estado concreto.
- Las transiciones entre estados deben ser correctas.
- El contexto debe enviarse al State.
- No debe haber clases de estado vacías.

Aplicado al proyecto:

- `Scrim` debe ser el contexto del patrón State.
- `Scrim` debe tener un atributo `estado: ScrimState`.
- `Scrim` delega operaciones como `postular`, `confirmar`, `iniciar`, `finalizar` y `cancelar` al estado actual.
- Las clases `BuscandoJugadoresState`, `LobbyArmadoState`, `ConfirmadoState`, `EnJuegoState`, `FinalizadoState` y `CanceladoState` deben implementar comportamiento real.

Ejemplo esperado:

```text
Scrim -> ScrimState : delega comportamiento en
```

## 10. Checklist de revisión antes de entregar

### Diagrama de clases

- [ ] No hay clases vacías.
- [ ] Los atributos son privados.
- [ ] Los métodos tienen parámetros cuando corresponde.
- [ ] Los métodos tienen tipo de retorno.
- [ ] Las relaciones tienen cardinalidad.
- [ ] Las relaciones tienen frase verbal.
- [ ] Las relaciones son coherentes con el código.
- [ ] Las interfaces están marcadas como interfaces.
- [ ] Las clases abstractas están marcadas como abstractas.
- [ ] Los patrones se ven claramente en el diagrama.

### Código

- [ ] El código coincide con el diagrama.
- [ ] Los patrones están implementados realmente.
- [ ] No hay clases decorativas sin responsabilidad.
- [ ] No hay lógica fuerte de negocio en controllers.
- [ ] No hay atributos públicos innecesarios.
- [ ] No hay clases con responsabilidades excesivas.
- [ ] El proyecto compila.
- [ ] Las demos/tests corren correctamente.

### Justificación de patrones

- [ ] Cada patrón tiene una justificación clara.
- [ ] La justificación explica el problema resuelto.
- [ ] La justificación menciona las clases participantes.
- [ ] La justificación coincide con el código y el diagrama.
- [ ] No se justifican patrones que no estén realmente implementados.

## 11. Criterio operativo para este TPO

Para este proyecto, se deben revisar especialmente estos patrones:

- State: ciclo de vida de `Scrim`.
- Strategy: estrategias de matchmaking.
- Observer: eventos de dominio y notificaciones.
- Facade: `EscrimsFacade` como acceso simplificado al sistema.
- Command: acciones reversibles sobre lobby/equipos.
- Chain of Responsibility: procesamiento de reportes.
- Abstract Factory: creación de notificadores.
- Adapter: integración con servicios externos.
- Builder: construcción flexible de `Scrim`.
- Singleton: `DomainEventBus`.

## 12. Nota de consistencia

El documento de evaluación se enfoca en el diagrama, los patrones, GRASP y SOLID. Para este TPO, además se acordó que el código debe quedar alineado con el diagrama final del proyecto.

Por lo tanto:

- Si el diagrama modela un concepto como clase, el código debería reflejarlo como clase.
- Si el código implementa un patrón, el diagrama debería mostrarlo.
- Si una funcionalidad está solo simulada, la documentación debe aclararlo.
- No se debe prometer en README, casos de uso o justificación algo que no exista en código.
