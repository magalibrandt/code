# Instrucciones de Compilación y Ejecución - eScrims

## Problema Detectado

El error que estás experimentando indica que el código está siendo compilado en una estructura de paquetes incorrecta. El error muestra:

\`\`\`
main.java.com.escrims.Main
\`\`\`

Cuando debería ser:

\`\`\`
com.escrims.Main
\`\`\`

## Solución

### Opción 1: Usar los Scripts Proporcionados (RECOMENDADO)

#### En Linux/Mac:

1. **Dar permisos de ejecución a los scripts:**
   \`\`\`bash
   chmod +x compile.sh run.sh
   \`\`\`

2. **Compilar el proyecto:**
   \`\`\`bash
   ./compile.sh
   \`\`\`

3. **Ejecutar el programa:**
   \`\`\`bash
   ./run.sh
   \`\`\`

#### En Windows:

1. **Compilar el proyecto:**
   \`\`\`cmd
   compile.bat
   \`\`\`

2. **Ejecutar el programa:**
   \`\`\`cmd
   run.bat
   \`\`\`

### Opción 2: Compilación Manual

#### Desde la raíz del proyecto:

**Compilar:**
\`\`\`bash
javac -d out -sourcepath src/main/java src/main/java/com/escrims/**/*.java src/main/java/com/escrims/*.java
\`\`\`

**Ejecutar:**
\`\`\`bash
java -cp out com.escrims.Main
\`\`\`

### Opción 3: Usar un IDE

#### Eclipse:

1. Abre Eclipse
2. File → New → Java Project
3. Desmarca "Use default location"
4. Selecciona la carpeta del proyecto
5. En "Project layout", selecciona "Create separate folders for sources and class files"
6. Click derecho en el proyecto → Properties → Java Build Path
7. En "Source" tab, asegúrate que la carpeta source sea `src/main/java`
8. En "Default output folder", selecciona `bin` o `out`
9. Click derecho en Main.java → Run As → Java Application

#### IntelliJ IDEA:

1. Abre IntelliJ IDEA
2. File → Open → Selecciona la carpeta del proyecto
3. IntelliJ debería detectar automáticamente la estructura Maven/Gradle
4. Si no, marca `src/main/java` como "Sources Root" (click derecho → Mark Directory as → Sources Root)
5. Click derecho en Main.java → Run 'Main.main()'

#### VS Code:

1. Instala la extensión "Extension Pack for Java"
2. Abre la carpeta del proyecto
3. VS Code debería detectar automáticamente la estructura
4. Presiona F5 o usa el botón "Run" en Main.java

## Estructura Correcta del Proyecto

\`\`\`
TPO ADO/
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── escrims/
│                   ├── Main.java
│                   ├── application/
│                   ├── domain/
│                   └── infrastructure/
├── out/                    (generado al compilar)
│   └── com/
│       └── escrims/
│           └── *.class
├── compile.sh
├── run.sh
├── compile.bat
├── run.bat
└── README.md
\`\`\`

## Verificación

Después de compilar correctamente, deberías ver:

\`\`\`
=== eScrims: Sistema de Organización de Scrims ===

1. Configurando sistema de notificaciones...
2. Configurando sistema de eventos...
...
=== Sistema funcionando correctamente ===
\`\`\`

## Problemas Comunes

### Error: "package com.escrims does not exist"
- **Causa:** Estás compilando desde el directorio incorrecto
- **Solución:** Asegúrate de estar en la raíz del proyecto (donde está compile.sh)

### Error: "class file has wrong version"
- **Causa:** Versión de Java incompatible
- **Solución:** Usa Java 11 o superior. Verifica con `java -version`

### Error: "cannot find symbol"
- **Causa:** Falta compilar alguna clase
- **Solución:** Usa los scripts proporcionados que compilan todas las clases

## Limpieza

Para limpiar los archivos compilados:

**Linux/Mac:**
\`\`\`bash
rm -rf out/
\`\`\`

**Windows:**
\`\`\`cmd
rmdir /s /q out
\`\`\`

## Notas Importantes

1. **NO uses** la carpeta `bin` con estructura `main.java.com.escrims`
2. **Siempre compila** desde la raíz del proyecto
3. **El classpath** debe apuntar a `out` (donde están los .class)
4. **La clase principal** es `com.escrims.Main` (sin prefijo "main.java")

## Soporte

Si sigues teniendo problemas:
1. Verifica tu versión de Java: `java -version` (debe ser 11+)
2. Verifica que estás en el directorio correcto: `pwd` o `cd`
3. Elimina la carpeta `bin` si existe
4. Usa los scripts proporcionados
