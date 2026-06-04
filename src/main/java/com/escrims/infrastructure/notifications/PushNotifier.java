package com.escrims.infrastructure.notifications;

import com.escrims.domain.model.Usuario;

/**
 * PATRÓN ABSTRACT FACTORY - Producto Concreto
 * Notificador para notificaciones push.
 */
public class PushNotifier implements Notifier {
    
    @Override
    public void send(Usuario destinatario, String titulo, String mensaje) {
        // Simulación de envío push (Firebase Cloud Messaging)
        System.out.println("[PUSH] Enviando a " + destinatario.getUsername());
        System.out.println("Título: " + titulo);
        System.out.println("Mensaje: " + mensaje);
        // En producción: integración con Firebase
    }
    
    @Override
    public String getChannelName() {
        return "Push";
    }
}
