package com.escrims.infrastructure.notifications;

/**
 * PATRÓN ABSTRACT FACTORY - Factory Concreta
 * Crea notificadores para entorno de producción (reales).
 */
public class ProdNotifierFactory implements NotifierFactory {
    
    @Override
    public Notifier createPushNotifier() {
        return new PushNotifier();
    }
    
    @Override
    public Notifier createEmailNotifier() {
        return new EmailNotifier();
    }
    
    @Override
    public Notifier createDiscordNotifier() {
        return new DiscordNotifier();
    }
}
