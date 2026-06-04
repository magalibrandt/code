@echo off
REM Script de compilación para eScrims (Windows)
echo === Compilando proyecto eScrims ===

REM Crear directorio de salida si no existe
if not exist out mkdir out

REM Compilar todos los archivos Java
echo Compilando archivos Java...
javac -d out -sourcepath src\main\java src\main\java\com\escrims\Main.java src\main\java\com\escrims\application\*.java src\main\java\com\escrims\application\builder\*.java src\main\java\com\escrims\domain\model\*.java src\main\java\com\escrims\domain\state\*.java src\main\java\com\escrims\domain\strategy\*.java src\main\java\com\escrims\domain\events\*.java src\main\java\com\escrims\infrastructure\notifications\*.java

if %errorlevel% equ 0 (
    echo Compilacion exitosa!
    echo.
    echo Para ejecutar el programa, usa:
    echo run.bat
) else (
    echo Error en la compilacion
    exit /b 1
)
