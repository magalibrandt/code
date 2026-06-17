# Instrucciones de Compilacion y Ejecucion - eScrims

## Requisitos

- Java 11 o superior.
- No requiere dependencias externas.
- Ejecutar los comandos desde la raiz del proyecto.

## Compilacion

### Windows

```cmd
compile.bat
```

### Linux/Mac

```bash
chmod +x compile.sh run.sh
./compile.sh
```

### Compilacion manual

```bash
javac -d out -sourcepath src/main/java src/main/java/com/escrims/**/*.java src/main/java/com/escrims/*.java
```

## Ejecucion

### Programa principal

```bash
java -cp out com.escrims.Main
```

Tambien puede ejecutarse con:

```cmd
run.bat
```

o:

```bash
./run.sh
```

### Demos y tests

```bash
java -cp out com.escrims.EjemploConFacade
java -cp out com.escrims.DemoCompleta
java -cp out com.escrims.tests.PatternTests
```

`PatternTests` no usa JUnit ni requiere `-ea`; es una clase ejecutable con `main` y validaciones propias que lanzan `AssertionError`.

## Salida Esperada de Main

La salida exacta puede variar por IDs, orden de mensajes y texto de notificaciones, pero debe mostrar aproximadamente:

```text
=== eScrims: Sistema de Organizacion de Scrims ===

>>> FACADE: interfaz unificada de aplicacion <<<

Scrim creado: Valorant - Estado: Buscando Jugadores

>>> STATE: postulaciones y transiciones <<<
...
Estado luego de completar cupos: Lobby Armado
Estado luego de confirmaciones: Confirmado
Estado luego de iniciar: En Juego

>>> STRATEGY: cambio de algoritmo <<<
Seleccionados por MMR: ...
Seleccionados por latencia: ...

>>> OBSERVER: notificaciones por EventBus <<<
Los eventos de cambio de estado fueron publicados automaticamente por los States.
NotificationSubscriber recibio esos eventos y envio notificaciones mediante NotifierFactory.

=== Demo principal finalizada ===
```

Las notificaciones se imprimen por consola porque las integraciones externas estan simuladas. `Main` y `EjemploConFacade` muestran el uso limpio de `EscrimsFacade`; `DemoCompleta` muestra patrones adicionales e instancia adapters directamente con fines demostrativos.

## Estructura Correcta del Proyecto

```text
src/
└── main/
    └── java/
        └── com/
            └── escrims/
                ├── Main.java
                ├── DemoCompleta.java
                ├── EjemploConFacade.java
                ├── application/
                ├── domain/
                ├── infrastructure/
                └── tests/
out/                       # generado al compilar
compile.sh
run.sh
compile.bat
run.bat
README.md
```

## Problemas Comunes

### `package com.escrims does not exist`

Causa probable: compilacion desde un directorio incorrecto.

Solucion: ejecutar desde la raiz del proyecto y usar `compile.bat` o `./compile.sh`.

### `class file has wrong version`

Causa probable: version de Java incompatible.

Solucion: verificar con:

```bash
java -version
```

### `cannot find symbol`

Causa probable: faltan clases por compilar.

Solucion: usar los scripts de compilacion, que compilan todos los paquetes del proyecto.

## Limpieza

### Windows

```cmd
rmdir /s /q out
```

### Linux/Mac

```bash
rm -rf out/
```

