package com.escrims.domain.model;

import java.util.Objects;

public class Sancion {
    private static final String NINGUNA = "Ninguna";
    private static final String ADVERTENCIA = "Advertencia";
    private static final String SUSPENSION_24H = "Suspension 24h";
    private static final String SUSPENSION_7D = "Suspension 7d";
    private static final String BAN_PERMANENTE = "Ban permanente";

    private final String nombre;
    private final String descripcion;
    private final int duracionDias;

    public Sancion(String nombre, String descripcion, int duracionDias) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la sancion es obligatorio");
        }
        if (descripcion == null) {
            throw new IllegalArgumentException("La descripcion de la sancion es obligatoria");
        }
        if (duracionDias < 0) {
            throw new IllegalArgumentException("La duracion no puede ser negativa");
        }
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.duracionDias = duracionDias;
    }

    public static Sancion ninguna() {
        return new Sancion(NINGUNA, "Sin sancion aplicada", 0);
    }

    public static Sancion advertencia() {
        return new Sancion(ADVERTENCIA, "Advertencia formal", 0);
    }

    public static Sancion suspension24h() {
        return new Sancion(SUSPENSION_24H, "Suspension temporal por 24 horas", 1);
    }

    public static Sancion suspension7d() {
        return new Sancion(SUSPENSION_7D, "Suspension temporal por siete dias", 7);
    }

    public static Sancion banPermanente() {
        return new Sancion(BAN_PERMANENTE, "Bloqueo permanente de la plataforma", 3650);
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getDuracionDias() {
        return duracionDias;
    }

    public boolean requiereCooldown() {
        return duracionDias > 0;
    }

    public boolean esNinguna() {
        return NINGUNA.equalsIgnoreCase(nombre);
    }

    public boolean esAdvertencia() {
        return ADVERTENCIA.equalsIgnoreCase(nombre);
    }

    @Override
    public String toString() {
        return nombre;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Sancion)) return false;
        Sancion other = (Sancion) obj;
        return duracionDias == other.duracionDias
            && nombre.equalsIgnoreCase(other.nombre)
            && descripcion.equals(other.descripcion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre.toLowerCase(), descripcion, duracionDias);
    }
}
