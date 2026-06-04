package com.escrims.infrastructure.notifications;

import com.escrims.domain.model.Usuario;

/**
 * PATRÓN ABSTRACT FACTORY - Producto Concreto
 * Notificador para Discord.
 */
public class DiscordNotifier implements Notifier {
    
    @Override
    public void send(Usuario destinatario, String titulo, String mensaje) {
        // Simulación de envío Discord (Webhook)
        System.out.println("[DISCORD] Enviando a " + destinatario.getUsername());
        System.out.println("**" + titulo + "**");
        System.out.println(mensaje);
        // En producción: integración con Discord Webhook API
    }
    
    @Override
    public String getChannelName() {
        return "Discord";
    }
}
