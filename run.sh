#!/bin/bash

# Script de ejecución para eScrims
echo "=== Ejecutando eScrims ==="
echo ""

# Verificar que exista el directorio out
if [ ! -d "out" ]; then
    echo "Error: No se encontró el directorio 'out'. Ejecuta primero ./compile.sh"
    exit 1
fi

# Ejecutar el programa
java -cp out com.escrims.Main
