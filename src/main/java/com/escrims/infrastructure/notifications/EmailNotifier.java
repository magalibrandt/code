package com.escrims.infrastructure.notifications;

import com.escrims.domain.model.Usuario;

/**
 * PATRÓN ABSTRACT FACTORY - Producto Concreto
 * Notificador para emails.
 */
public class EmailNotifier implements Notifier {
    
    @Override
    public void send(Usuario destinatario, String titulo, String mensaje) {
        // Simulación de envío email (JavaMail/SendGrid)
        System.out.println("[EMAIL] Enviando a " + destinatario.getEmail());
        System.out.println("Asunto: " + titulo);
        System.out.println("Cuerpo: " + mensaje);
        // En producción: integración con SendGrid/JavaMail
    }
    
    @Override
    public String getChannelName() {
        return "Email";
    }
}
