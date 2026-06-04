package com.escrims.domain.events;

/**
 * PATRÓN OBSERVER - Observer Interface
 * Interface para todos los suscriptores de eventos.
 */
public interface Subscriber {
    void onEvent(DomainEvent event);
    String getSubscriberName();
}
