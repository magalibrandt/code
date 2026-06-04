package com.escrims.application.builder;

import com.escrims.domain.model.*;
import java.time.LocalDateTime;

/**
 * PATRÓN BUILDER
 * Construye objetos Scrim complejos paso a paso con validaciones.
 * Aplica GRASP Low Coupling: separa construcción de representación.
 */
public class ScrimBuilder {
    private Scrim scrim;
    
    public ScrimBuilder(String juego, String formato, String region, Usuario creador) {
        this.scrim = new Scrim(juego, formato, region, creador);
    }
    
    public ScrimBuilder conRangos(String rangoMin, String rangoMax) {
        if (rangoMin == null || rangoMax == null) {
            throw new IllegalArgumentException("Rangos no pueden ser nulos");
        }
        scrim.setRangoMin(rangoMin);
        scrim.setRangoMax(rangoMax);
        return this;
    }
    
    public ScrimBuilder conLatenciaMaxima(int latenciaMax) {
        if (latenciaMax <= 0 || latenciaMax > 500) {
            throw new IllegalArgumentException("Latencia debe estar entre 1 y 500ms");
        }
        scrim.setLatenciaMax(latenciaMax);
        return this;
    }
    
    public ScrimBuilder conFechaHora(LocalDateTime fechaHora) {
        if (fechaHora.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha no puede ser en el pasado");
        }
        scrim.setFechaHora(fechaHora);
        return this;
    }
    
    public ScrimBuilder conCupos(int cuposTotales) {
        if (cuposTotales <= 0 || cuposTotales > 20) {
            throw new IllegalArgumentException("Cupos debe estar entre 1 y 20");
        }
        scrim.setCuposTotales(cuposTotales);
        return this;
    }
    
    public ScrimBuilder conModalidad(String modalidad) {
        if (!modalidad.equals("ranked-like") && 
            !modalidad.equals("casual") && 
            !modalidad.equals("practica")) {
            throw new IllegalArgumentException("Modalidad inválida");
        }
        scrim.setModalidad(modalidad);
        return this;
    }
    
    public ScrimBuilder conDuracion(int duracionMinutos) {
        if (duracionMinutos <= 0) {
            throw new IllegalArgumentException("Duración debe ser positiva");
        }
        scrim.setDuracionEstimada(duracionMinutos);
        return this;
    }
    
    public Scrim build() {
        validarInvariantes();
        return scrim;
    }
    
    private void validarInvariantes() {
        if (scrim.getFechaHora() == null) {
            throw new IllegalStateException("Debe especificar fecha y hora");
        }
        if (scrim.getCuposTotales() == 0) {
            throw new IllegalStateException("Debe especificar cupos totales");
        }
        if (scrim.getModalidad() == null) {
            throw new IllegalStateException("Debe especificar modalidad");
        }
    }
}
