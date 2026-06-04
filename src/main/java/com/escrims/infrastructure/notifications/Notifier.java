package com.escrims.infrastructure.notifications;

import com.escrims.domain.model.Usuario;

/**
 * PATRÓN ABSTRACT FACTORY - Producto Abstracto
 * Interface para todos los notificadores.
 */
public interface Notifier {
    void send(Usuario destinatario, String titulo, String mensaje);
    String getChannelName();
}
