package com.escrims.infrastructure.notifications;

import com.escrims.domain.model.Usuario;

/**
 * PATRÓN ABSTRACT FACTORY - Factory Concreta
 * Crea notificadores para entorno de desarrollo (simulados).
 */
public class DevNotifierFactory implements NotifierFactory {
    
    @Override
    public Notifier createPushNotifier() {
        return new Notifier() {
            @Override
            public void send(Usuario destinatario, String titulo, String mensaje) {
                System.out.println("[DEV-PUSH] Mock notification to " + destinatario.getUsername());
            }
            
            @Override
            public String getChannelName() {
                return "Dev-Push";
            }
        };
    }
    
    @Override
    public Notifier createEmailNotifier() {
        return new Notifier() {
            @Override
            public void send(Usuario destinatario, String titulo, String mensaje) {
                System.out.println("[DEV-EMAIL] Mock notification to " + destinatario.getEmail());
            }
            
            @Override
            public String getChannelName() {
                return "Dev-Email";
            }
        };
    }
    
    @Override
    public Notifier createDiscordNotifier() {
        return new Notifier() {
            @Override
            public void send(Usuario destinatario, String titulo, String mensaje) {
                System.out.println("[DEV-DISCORD] Mock notification to " + destinatario.getUsername());
            }
            
            @Override
            public String getChannelName() {
                return "Dev-Discord";
            }
        };
    }
}
