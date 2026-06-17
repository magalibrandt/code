package com.escrims.domain.events;

import java.util.UUID;
import java.util.Date;

/**
 * PATRÓN OBSERVER - Evento de Dominio
 * Clase base para todos los eventos del sistema.
 */
public abstract class DomainEvent {
    private final UUID eventId;
    private final Date timestamp;
    
    public DomainEvent() {
        this.eventId = UUID.randomUUID();
        this.timestamp = new Date();
    }
    
    public UUID getEventId() { return eventId; }
    public Date getTimestamp() { return timestamp; }
    public UUID getId() { return eventId; }
    public Date getOccurredOn() { return timestamp; }
    
    public abstract String getEventType();
}
