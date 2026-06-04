package com.escrims;

import com.escrims.domain.model.*;
import com.escrims.domain.strategy.*;
import com.escrims.application.EscrimsFacade;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Ejemplo de uso simplificado del sistema usando FACADE PATTERN
 * 
 * Sin Facade: Cliente debe conocer y usar múltiples subsistemas
 * Con Facade: Cliente solo interactúa con una interfaz simple
 */
public class EjemploConFacade {
    
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║  EJEMPLO: PATRÓN FACADE - Interface Simplificada             ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝\n");
        
        // Crear Facade con estrategia por defecto
        EscrimsFacade facade = new EscrimsFacade(new ByMMRStrategy());
        
        System.out.println("┌─ PASO 1: Conectar servicios externos ───────────────────────┐");
        facade.conectarDiscord();
        facade.conectarSendGrid();
        facade.conectarICal();
        System.out.println();
        
        System.out.println("┌─ PASO 2: Crear usuarios ───────────────────────────────────┐");
        Usuario organizador = facade.crearUsuario("Organizador", "org@escrims.com", 
                                                  "password123", "LATAM");
        Usuario jugador1 = facade.crearUsuario("ProGamer", "pro@escrims.com", 
                                              "password456", "LATAM");
        Usuario jugador2 = facade.crearUsuario("Support", "sup@escrims.com", 
                                              "password789", "LATAM");
        
        System.out.println("✓ Usuarios creados exitosamente\n");
        
        System.out.println("┌─ PASO 3: Crear Scrim (usando Builder interno) ──────────────┐");
        Scrim scrim = facade.crearScrim("Valorant", "5v5", "LATAM", 
                                        organizador.getId(), 
                                        "Gold", "Platinum", 80, 
                                        LocalDateTime.now().plusHours(2), 
                                        10, "ranked-like");
        System.out.println("✓ Scrim creado: " + scrim.getJuego() + " " + scrim.getFormato() + "\n");
        
        System.out.println("┌─ PASO 4: Buscar scrims disponibles ────────────────────────┐");
        List<Scrim> scrims = facade.buscarScrims("Valorant", "5v5", "LATAM", 
                                                  "Gold", "Platinum", 80);
        System.out.println("✓ Scrims encontrados: " + scrims.size() + "\n");
        
        System.out.println("┌─ PASO 5: Postulaciones a scrim ────────────────────────────┐");
        // Simular más usuarios
        for (int i = 1; i <= 9; i++) {
            Usuario u = facade.crearUsuario("Player" + i, "p" + i + "@test.com", 
                                           "pass" + i, "LATAM");
            facade.postularseAScrim(scrim.getId(), u.getId(), "Duelist");
        }
        System.out.println("✓ Jugadores postulados\n");
        
        System.out.println("┌─ PASO 6: Confirmación de participantes ────────────────────┐");
        System.out.println("→ Confirmando a " + jugador1.getUsername());
        facade.confirmarParticipacion(scrim.getId(), jugador1.getId());
        
        System.out.println("→ Confirmando a " + jugador2.getUsername());
        facade.confirmarParticipacion(scrim.getId(), jugador2.getId());
        System.out.println("✓ Todos confirmaron\n");
        
        System.out.println("┌─ PASO 7: Operaciones reversibles (Comandos) ───────────────┐");
        System.out.println("→ Asignando rol 'Duelist' a " + jugador1.getUsername());
        facade.asignarRol(scrim.getId(), jugador1.getId(), "Duelist");
        
        System.out.println("→ Invitando jugador específico");
        facade.invitarJugador(scrim.getId(), jugador2.getId(), "Support");
        System.out.println("✓ Operaciones registradas\n");
        
        System.out.println("┌─ PASO 8: Iniciar y Finalizar Scrim ─────────────────────────┐");
        facade.iniciarScrim(scrim.getId());
        System.out.println("✓ Scrim iniciado");
        
        facade.finalizarScrim(scrim.getId());
        System.out.println("✓ Scrim finalizado\n");
        
        System.out.println("┌─ PASO 9: Integraciones externas ───────────────────────────┐");
        facade.enviarMensajeDiscord("escrims", "✓ Nuevo scrim finalizado: Valorant 5v5");
        facade.enviarEmail(jugador1.getEmail(), "Scrim Finalizado", 
                          "Tu scrim de Valorant ha finalizado. ¡Buen juego!");
        facade.exportarEventoCalendario("Valorant Scrim", 
                                       LocalDateTime.now().plusHours(2), 45);
        System.out.println("✓ Notificaciones enviadas\n");
        
        System.out.println("┌─ PASO 10: Moderación (si es necesario) ───────────────────┐");
        facade.reportarConductaInapropiada(scrim.getId(), jugador1.getId(), 
                                          jugador2.getId(), "LENGUAJE_OFENSIVO", 
                                          "Comportamiento inapropiado");
        System.out.println("✓ Reporte procesado con escalación automática\n");
        
        System.out.println("┌─ PASO 11: Obtener información ─────────────────────────────┐");
        Map<String, Object> infoJugador = facade.obtenerInfoUsuario(jugador1.getId());
        System.out.println("Información de " + jugador1.getUsername() + ":");
        infoJugador.forEach((k, v) -> System.out.println("  " + k + ": " + v));
        
        Map<String, Object> infoScrim = facade.obtenerInfoScrim(scrim.getId());
        System.out.println("\nInformación del Scrim:");
        infoScrim.forEach((k, v) -> System.out.println("  " + k + ": " + v));
        System.out.println();
        
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║           FAÇADE SIMPLIFICA LA INTERFAZ DEL SISTEMA          ║");
        System.out.println("╠═══════════════════════════════════════════════════════════════╣");
        System.out.println("║ Sin Facade: Cliente debe conocer:                            ║");
        System.out.println("║   - ScrimService, DomainEventBus, CommandInvoker             ║");
        System.out.println("║   - NotificationSubscriber, ReportProcessor                  ║");
        System.out.println("║   - Múltiples Adapters y Factories                           ║");
        System.out.println("║                                                               ║");
        System.out.println("║ Con Facade: Cliente solo usa EscrimsFacade                   ║");
        System.out.println("║   - Interface simple y unificada                              ║");
        System.out.println("║   - Encapsula toda la complejidad                             ║");
        System.out.println("║   - Bajo acoplamiento                                         ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝\n");
    }
}
