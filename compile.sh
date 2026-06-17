#!/bin/bash

# Script de compilación para eScrims
echo "=== Compilando proyecto eScrims ==="

# Crear directorio de salida si no existe
mkdir -p out

# Compilar todos los archivos Java
echo "Compilando archivos Java..."
find src/main/java -name "*.java" > sources.txt
javac -d out -sourcepath src/main/java @sources.txt
rm -f sources.txt

if [ $? -eq 0 ]; then
    echo "✓ Compilación exitosa!"
    echo ""
    echo "Para ejecutar el programa, usa:"
    echo "./run.sh"
else
    echo "✗ Error en la compilación"
    exit 1
fi
