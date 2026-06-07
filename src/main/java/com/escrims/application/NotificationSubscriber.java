package com.escrims.application;

import com.escrims.domain.events.*;
import com.escrims.domain.model.*;
import com.escrims.infrastructure.notifications.*;
import java.util.*;

/**
 * PATRÓN OBSERVER - Observer Concreto
 * Suscriptor que escucha eventos y envía notificaciones.
 */
public class NotificationSubscriber implements Subscriber {
    private final NotifierFactory notifierFactory;
    private final Map<UUID, Scrim> scrimRepository; // Simulación de repositorio
    private final Map<UUID, Usuario> usuarioRepository; // Simulación de repositorio
    
    public NotificationSubscriber(NotifierFactory notifierFactory) {
        this.notifierFactory = notifierFactory;
        this.scrimRepository = new HashMap<>();
        this.usuarioRepository = new HashMap<>();
    }
    
    @Override
    public void onEvent(DomainEvent event) {
        if (event instanceof ScrimStateChangedEvent) {
            handleScrimStateChanged((ScrimStateChangedEvent) event);
        } else if (event instanceof PostulacionAceptadaEvent) {
            handlePostulacionAceptada((PostulacionAceptadaEvent) event);
        }
    }
    
    private void handleScrimStateChanged(ScrimStateChangedEvent event) {
        Scrim scrim = scrimRepository.get(event.getScrimId());
        if (scrim == null) return;
        
        String mensaje = "El scrim '" + scrim.getJuego() + "' cambió de estado: "
            + event.getEstadoAnterior()
            + " -> "
            + event.getEstadoNuevo();
        
        // Notificar a todos los participantes
        scrim.getPostulaciones().stream()
            .filter(p -> p.getEstado().esAceptada())
            .forEach(p -> {
                Usuario usuario = p.getUsuario();
                enviarNotificaciones(usuario, "Actualización de Scrim", mensaje);
            });
    }
    
    private void handlePostulacionAceptada(PostulacionAceptadaEvent event) {
        Usuario usuario = usuarioRepository.get(event.getUsuarioId());
        Scrim scrim = scrimRepository.get(event.getScrimId());
        
        if (usuario == null || scrim == null) return;
        
        String mensaje = "Tu postulación al scrim de " + scrim.getJuego() + " fue aceptada!";
        enviarNotificaciones(usuario, "Postulación Aceptada", mensaje);
    }
    
    private void enviarNotificaciones(Usuario usuario, String titulo, String mensaje) {
        if (usuario.getPreferencias().isRecibirNotificacionesPush()) {
            Notifier pushNotifier = notifierFactory.createPushNotifier();
            pushNotifier.send(usuario, titulo, mensaje);
        }
        
        if (usuario.getPreferencias().isRecibirNotificacionesEmail()) {
            Notifier emailNotifier = notifierFactory.createEmailNotifier();
            emailNotifier.send(usuario, titulo, mensaje);
        }
        
        if (usuario.getPreferencias().isRecibirNotificacionesDiscord()) {
            Notifier discordNotifier = notifierFactory.createDiscordNotifier();
            discordNotifier.send(usuario, titulo, mensaje);
        }
    }
    
    public void registrarScrim(Scrim scrim) {
        scrimRepository.put(scrim.getId(), scrim);
    }
    
    public void registrarUsuario(Usuario usuario) {
        usuarioRepository.put(usuario.getId(), usuario);
    }
    
    @Override
    public String getSubscriberName() {
        return "NotificationSubscriber";
    }
}
