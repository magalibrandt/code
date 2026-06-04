@echo off
REM Script de ejecución para eScrims (Windows)
echo === Ejecutando eScrims ===
echo.

REM Verificar que exista el directorio out
if not exist out (
    echo Error: No se encontro el directorio 'out'. Ejecuta primero compile.bat
    exit /b 1
)

REM Ejecutar el programa
java -cp out com.escrims.Main
