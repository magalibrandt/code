package com.escrims.domain.model;

import java.util.Objects;

public class EstadoReporte {
    private static final String PENDIENTE = "Pendiente";
    private static final String EN_REVISION = "En revision";
    private static final String RESUELTO = "Resuelto";
    private static final String DESCARTADO = "Descartado";

    private final String nombre;

    public EstadoReporte(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del estado de reporte es obligatorio");
        }
        this.nombre = nombre;
    }

    public static EstadoReporte pendiente() {
        return new EstadoReporte(PENDIENTE);
    }

    public static EstadoReporte enRevision() {
        return new EstadoReporte(EN_REVISION);
    }

    public static EstadoReporte resuelto() {
        return new EstadoReporte(RESUELTO);
    }

    public static EstadoReporte descartado() {
        return new EstadoReporte(DESCARTADO);
    }

    public String getNombre() {
        return nombre;
    }

    public boolean esPendiente() {
        return PENDIENTE.equalsIgnoreCase(nombre);
    }

    public boolean esEnRevision() {
        return EN_REVISION.equalsIgnoreCase(nombre);
    }

    public boolean esResuelto() {
        return RESUELTO.equalsIgnoreCase(nombre);
    }

    public boolean esDescartado() {
        return DESCARTADO.equalsIgnoreCase(nombre);
    }

    @Override
    public String toString() {
        return nombre;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof EstadoReporte)) return false;
        EstadoReporte other = (EstadoReporte) obj;
        return nombre.equalsIgnoreCase(other.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre.toLowerCase());
    }
}
