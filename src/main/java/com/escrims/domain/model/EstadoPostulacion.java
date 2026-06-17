package com.escrims.domain.model;

import java.util.Objects;

public class EstadoPostulacion {
    private static final String PENDIENTE = "Pendiente";
    private static final String ACEPTADA = "Aceptada";
    private static final String RECHAZADA = "Rechazada";

    private final String nombre;

    public EstadoPostulacion(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del estado de postulacion es obligatorio");
        }
        this.nombre = nombre;
    }

    public static EstadoPostulacion pendiente() {
        return new EstadoPostulacion(PENDIENTE);
    }

    public static EstadoPostulacion aceptada() {
        return new EstadoPostulacion(ACEPTADA);
    }

    public static EstadoPostulacion rechazada() {
        return new EstadoPostulacion(RECHAZADA);
    }

    public String getNombre() {
        return nombre;
    }

    public boolean esPendiente() {
        return PENDIENTE.equalsIgnoreCase(nombre);
    }

    public boolean esAceptada() {
        return ACEPTADA.equalsIgnoreCase(nombre);
    }

    public boolean esRechazada() {
        return RECHAZADA.equalsIgnoreCase(nombre);
    }

    @Override
    public String toString() {
        return nombre;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof EstadoPostulacion)) return false;
        EstadoPostulacion other = (EstadoPostulacion) obj;
        return nombre.equalsIgnoreCase(other.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre.toLowerCase());
    }
}
