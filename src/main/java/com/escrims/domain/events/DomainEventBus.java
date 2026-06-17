package com.escrims.domain.events;

import java.util.*;

/**
 * PATRÓN OBSERVER - Subject/Publisher
 * Bus de eventos que gestiona suscriptores y publica eventos.
 * Implementado como Singleton para acceso global.
 */
public class DomainEventBus {
    private static DomainEventBus instance;
    private final List<Subscriber> subscribers;
    
    private DomainEventBus() {
        this.subscribers = new ArrayList<>();
    }
    
    public static synchronized DomainEventBus getInstance() {
        if (instance == null) {
            instance = new DomainEventBus();
        }
        return instance;
    }
    
    public void subscribe(Subscriber subscriber) {
        if (!subscribers.contains(subscriber)) {
            subscribers.add(subscriber);
        }
    }
    
    public void unsubscribe(Subscriber subscriber) {
        subscribers.remove(subscriber);
    }
    
    public void publish(DomainEvent event) {
        for (Subscriber subscriber : subscribers) {
            try {
                subscriber.onEvent(event);
            } catch (RuntimeException e) {
                System.err.println("[EVENT BUS] Subscriber fallo: " + subscriber.getSubscriberName() + " - " + e.getMessage());
            }
        }
    }
    
    public void clearSubscribers() {
        subscribers.clear();
    }
}
