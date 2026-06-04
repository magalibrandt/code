package com.escrims.domain.events;

import java.util.UUID;

/**
 * PATRÓN OBSERVER - Evento Concreto
 * Se dispara cuando un scrim cambia de estado.
 */
public class ScrimStateChangedEvent extends DomainEvent {
    private final UUID scrimId;
    private final String nuevoEstado;
    
    public ScrimStateChangedEvent(UUID scrimId, String nuevoEstado) {
        super();
        this.scrimId = scrimId;
        this.nuevoEstado = nuevoEstado;
    }
    
    public UUID getScrimId() { return scrimId; }
    public String getNuevoEstado() { return nuevoEstado; }
    
    @Override
    public String getEventType() {
        return "ScrimStateChanged";
    }
}
