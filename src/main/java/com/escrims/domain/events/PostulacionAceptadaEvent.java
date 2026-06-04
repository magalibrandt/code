package com.escrims.domain.events;

import java.util.UUID;

/**
 * PATRÓN OBSERVER - Evento Concreto
 * Se dispara cuando una postulación es aceptada.
 */
public class PostulacionAceptadaEvent extends DomainEvent {
    private final UUID scrimId;
    private final UUID usuarioId;
    
    public PostulacionAceptadaEvent(UUID scrimId, UUID usuarioId) {
        super();
        this.scrimId = scrimId;
        this.usuarioId = usuarioId;
    }
    
    public UUID getScrimId() { return scrimId; }
    public UUID getUsuarioId() { return usuarioId; }
    
    @Override
    public String getEventType() {
        return "PostulacionAceptada";
    }
}
