package com.escrims.domain.events;

import java.util.UUID;

/**
 * PATRÓN OBSERVER - Evento Concreto
 * Se dispara cuando un scrim cambia de estado.
 */
public class ScrimStateChangedEvent extends DomainEvent {
    private final UUID scrimId;
    private final String estadoAnterior;
    private final String estadoNuevo;
    
    public ScrimStateChangedEvent(UUID scrimId, String estadoAnterior, String estadoNuevo) {
        super();
        this.scrimId = scrimId;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
    }
    
    public UUID getScrimId() { return scrimId; }
    public String getEstadoAnterior() { return estadoAnterior; }
    public String getEstadoNuevo() { return estadoNuevo; }
    
    @Override
    public String getEventType() {
        return "ScrimStateChanged";
    }
}
