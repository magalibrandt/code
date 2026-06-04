package com.escrims.infrastructure.notifications;

/**
 * PATRÓN ABSTRACT FACTORY - Factory Abstracta
 * Define la interfaz para crear familias de notificadores.
 */
public interface NotifierFactory {
    Notifier createPushNotifier();
    Notifier createEmailNotifier();
    Notifier createDiscordNotifier();
}
