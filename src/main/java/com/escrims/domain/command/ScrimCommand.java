package com.escrims.domain.command;

import com.escrims.domain.model.Scrim;

/**
 * PATRÓN COMMAND
 * Interface para comandos relacionados con Scrims.
 * Permite operaciones reversibles (undo) antes de confirmar.
 */
public interface ScrimCommand {
    /**
     * Ejecuta el comando en el contexto del Scrim.
     */
    void execute(Scrim scrim);
    
    /**
     * Revierte el comando al estado anterior.
     */
    void undo(Scrim scrim);
    
    /**
     * Obtiene descripción del comando.
     */
    String getDescription();
}
