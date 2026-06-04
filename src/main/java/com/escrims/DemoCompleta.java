package com.escrims;

import com.escrims.domain.model.*;
import com.escrims.domain.strategy.*;
import com.escrims.domain.events.*;
import com.escrims.domain.command.*;
import com.escrims.domain.moderacion.*;
import com.escrims.infrastructure.notifications.*;
import com.escrims.infrastructure.adapters.*;
import com.escrims.application.*;
import com.escrims.application.builder.ScrimBuilder;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Demostración completa de todos los patrones de diseño
 * y funcionalidades del sistema eScrims.
 */
public class DemoCompleta {
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║  eSCRIMS - DEMOSTRACIÓN COMPLETA DE PATRONES DE DISEÑO        ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");
        
        // ============ SETUP INICIAL ============
        System.out.println("┌─ SETUP INICIAL ─────────────────────────────────────────────────┐");
        
        // Abstract Factory - Crear notificadores según entorno
        NotifierFactory factory = new ProdNotifierFactory();
        
        // Observer - Event Bus
        DomainEventBus eventBus = DomainEventBus.getInstance();
        NotificationSubscriber notificationSubscriber = new NotificationSubscriber(factory);
        eventBus.subscribe(notificationSubscriber);
        
        // Adapter - Integraciones externas
        DiscordAdapter discordAdapter = new DiscordAdapter("webhook_url", "bot_token");
        SendGridAdapter sendgridAdapter = new SendGridAdapter("api_key", "noreply@escrims.com");
        ICalAdapter icalAdapter = new ICalAdapter("eScrims Calendar");
        
        discordAdapter.conectar();
        sendgridAdapter.conectar();
        icalAdapter.conectar();
        
        // Strategy - Servicio con estrategia de matchmaking
        MatchmakingStrategy strategy = new ByMMRStrategy();
        ScrimService scrimService = new ScrimService(strategy);
        
        System.out.println("✓ Sistema inicializado con:");
        System.out.println("  - Notificaciones: " + factory.getClass().getSimpleName());
        System.out.println("  - Estrategia: " + strategy.getNombreEstrategia());
        System.out.println("  - Adaptadores: Discord, SendGrid, iCal\n");
        
        // ============ CREAR USUARIOS ============
        System.out.println("┌─ CREAR USUARIOS ────────────────────────────────────────────────┐");
        
        Usuario organizador = new Usuario("Organizador", "org@escrims.com", "hash123", "LATAM");
        organizador.agregarRango("Valorant", "Platinum");
        organizador.agregarRolPreferido("IGL");
        
        Usuario jugador1 = new Usuario("ProGamer", "pro@escrims.com", "hash456", "LATAM");
        jugador1.agregarRango("Valorant", "Gold");
        jugador1.agregarRolPreferido("Duelist");
        
        Usuario jugador2 = new Usuario("Support", "sup@escrims.com", "hash789", "LATAM");
        jugador2.agregarRango("Valorant", "Platinum");
        jugador2.agregarRolPreferido("Support");
        
        Usuario jugador3 = new Usuario("Controller", "ctrl@escrims.com", "hashA", "LATAM");
        jugador3.agregarRango("Valorant", "Gold");
        jugador3.agregarRolPreferido("Controller");
        
        scrimService.registrarUsuario(organizador);
        scrimService.registrarUsuario(jugador1);
        scrimService.registrarUsuario(jugador2);
        scrimService.registrarUsuario(jugador3);
        
        notificationSubscriber.registrarUsuario(organizador);
        notificationSubscriber.registrarUsuario(jugador1);
        notificationSubscriber.registrarUsuario(jugador2);
        notificationSubscriber.registrarUsuario(jugador3);
        
        System.out.println("✓ Usuarios creados:\n");
        System.out.println("  Organizador: " + organizador.getUsername() + " (" + 
                          organizador.getRangoParaJuego("Valorant") + ")");
        System.out.println("  Jugador 1: " + jugador1.getUsername());
        System.out.println("  Jugador 2: " + jugador2.getUsername());
        System.out.println("  Jugador 3: " + jugador3.getUsername() + "\n");
        
        // ============ PATRÓN BUILDER - Crear Scrim ============
        System.out.println("┌─ PATRÓN BUILDER: Crear Scrim ──────────────────────────────────┐");
        
        Scrim scrim = new ScrimBuilder("Valorant", "5v5", "LATAM", organizador)
            .conRangos("Gold", "Platinum")
            .conLatenciaMaxima(80)
            .conFechaHora(LocalDateTime.now().plusHours(2))
            .conCupos(10)
            .conModalidad("ranked-like")
            .conDuracion(45)
            .build();
        
        notificationSubscriber.registrarScrim(scrim);
        System.out.println("✓ Scrim creado con Builder:");
        System.out.println("  Juego: " + scrim.getJuego() + " " + scrim.getFormato());
        System.out.println("  Rango: " + scrim.getRangoMin() + " - " + scrim.getRangoMax());
        System.out.println("  Estado: " + scrim.getNombreEstado() + "\n");
        
        // ============ PATRÓN STATE - Postulaciones y transiciones ============
        System.out.println("┌─ PATRÓN STATE: Ciclo de vida del Scrim ────────────────────────┐");
        System.out.println("Simulando postulaciones y transiciones de estado...\n");
        
        scrim.postular(jugador1, "Duelist");
        System.out.println("→ " + jugador1.getUsername() + " se postula (Estado: " + 
                          scrim.getNombreEstado() + ")");
        
        scrim.postular(jugador2, "Support");
        System.out.println("→ " + jugador2.getUsername() + " se postula (Estado: " + 
                          scrim.getNombreEstado() + ")");
        
        scrim.postular(jugador3, "Controller");
        System.out.println("→ " + jugador3.getUsername() + " se postula (Estado: " + 
                          scrim.getNombreEstado() + ")");
        
        // Simular más postulaciones
        for (int i = 4; i <= 10; i++) {
            Usuario u = new Usuario("Player" + i, "p" + i + "@test.com", "hash" + i, "LATAM");
            u.agregarRango("Valorant", "Gold");
            scrim.postular(u, "Sentinel");
        }
        
        System.out.println("✓ Cupo completo! Estado: " + scrim.getNombreEstado() + "\n");
        
        // ============ PATRÓN COMMAND - Operaciones reversibles ============
        System.out.println("┌─ PATRÓN COMMAND: Acciones reversibles ──────────────────────────┐");
        
        CommandInvoker invoker = new CommandInvoker(scrim);
        
        // Asignar rol
        invoker.ejecutar(new AsignarRolCommand(jugador1, "Duelist"));
        invoker.ejecutar(new AsignarRolCommand(jugador2, "Support"));
        
        // Intercambiar jugadores
        invoker.ejecutar(new SwapJugadoresCommand(jugador1, jugador3));
        
        // Deshacer
        System.out.println("\nDeshaciendo último comando...");
        invoker.deshacer();
        
        System.out.println("\nHistorial de comandos:");
        invoker.obtenerHistorial().forEach(cmd -> System.out.println("  - " + cmd));
        
        // ============ PATRÓN STRATEGY - Cambiar algoritmos ============
        System.out.println("\n┌─ PATRÓN STRATEGY: Algoritmos intercambiables ───────────────────┐");
        
        List<Usuario> candidatos = Arrays.asList(jugador1, jugador2, jugador3);
        
        MatchmakingStrategy[] estrategias = {
            new ByMMRStrategy(),
            new ByLatencyStrategy(),
            new ByHistoryStrategy()
        };
        
        for (MatchmakingStrategy est : estrategias) {
            scrimService.setMatchmakingStrategy(est);
            System.out.println("\n→ Usando: " + est.getNombreEstrategia());
            List<Usuario> seleccionados = scrimService.emparejarJugadores(scrim, candidatos);
            System.out.println("  Seleccionados: " + seleccionados.size() + " jugadores");
        }
        
        // ============ PATRÓN STATE - Confirmaciones ============
        System.out.println("\n┌─ PATRÓN STATE: Confirmación de participantes ──────────────────┐");
        
        for (Postulacion p : scrim.getPostulaciones()) {
            if (p.getEstado() == EstadoPostulacion.ACEPTADA) {
                scrim.confirmar(p.getUsuario());
                System.out.println("✓ " + p.getUsuario().getUsername() + " confirmó");
            }
        }
        System.out.println("\nEstado: " + scrim.getNombreEstado() + "\n");
        
        // ============ PATRÓN STATE - Iniciar y Finalizar ============
        System.out.println("┌─ PATRÓN STATE: Iniciar y Finalizar ────────────────────────────┐");
        
        scrim.iniciar();
        System.out.println("✓ Scrim iniciado! Estado: " + scrim.getNombreEstado());
        
        scrim.finalizar();
        System.out.println("✓ Scrim finalizado! Estado: " + scrim.getNombreEstado() + "\n");
        
        // ============ PATRÓN CHAIN OF RESPONSIBILITY - Moderación ============
        System.out.println("┌─ PATRÓN CHAIN OF RESPONSIBILITY: Moderación ────────────────────┐");
        
        ReporteConducta reporte = new ReporteConducta(scrim, jugador1, jugador3, "LENGUAJE_OFENSIVO",
            "El jugador usó lenguaje inapropiado durante el scrim");
        
        System.out.println("Procesando reporte de conducta...\n");
        scrimService.procesarReporte(reporte);
        
        System.out.println("\n✓ Reporte resuelto!");
        System.out.println("  Sanción: " + reporte.getSancion());
        System.out.println("  Procesador: " + reporte.getProcesadorResolvio() + "\n");
        
        // ============ PATRÓN ADAPTER - Integraciones ============
        System.out.println("┌─ PATRÓN ADAPTER: Integraciones externas ───────────────────────┐");
        
        discordAdapter.enviarMensaje("escrims", "✓ Nuevo scrim creado: Valorant 5v5");
        
        sendgridAdapter.enviarEmail(jugador1.getEmail(),
            "Confirmación de participación",
            "Tu scrim Valorant inicia en 2 horas");
        
        icalAdapter.exportarEvento("Valorant Scrim 5v5",
            LocalDateTime.now().plusHours(2), 45);
        
        // ============ PATRÓN OBSERVER - Eventos ============
        System.out.println("\n┌─ PATRÓN OBSERVER: Sistema de eventos ───────────────────────────┐");
        
        System.out.println("Publicando evento...");
        eventBus.publish(new ScrimStateChangedEvent(scrim.getId(), "Finalizado"));
        System.out.println("✓ Evento publicado a suscriptores\n");
        
        // ============ RESUMEN FINAL ============
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                    RESUMEN DE PATRONES                        ║");
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        System.out.println("║ ✓ STATE PATTERN - 6 estados del Scrim                         ║");
        System.out.println("║ ✓ STRATEGY PATTERN - 3 algoritmos intercambiables             ║");
        System.out.println("║ ✓ OBSERVER PATTERN - Sistema de eventos desacoplado           ║");
        System.out.println("║ ✓ ABSTRACT FACTORY - Notificadores por entorno                ║");
        System.out.println("║ ✓ COMMAND PATTERN - Acciones reversibles con undo/redo        ║");
        System.out.println("║ ✓ CHAIN OF RESPONSIBILITY - Moderación escalonada             ║");
        System.out.println("║ ✓ ADAPTER PATTERN - Integraciones externas                    ║");
        System.out.println("║ ✓ BUILDER PATTERN - Construcción validada                     ║");
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        System.out.println("║ PRINCIPIOS SOLID: ✓ SRP ✓ OCP ✓ LSP ✓ ISP ✓ DIP              ║");
        System.out.println("║ PRINCIPIOS GRASP: ✓ Expert ✓ Creator ✓ Controller            ║");
        System.out.println("║                  ✓ Low Coupling ✓ High Cohesion              ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");
    }
}
