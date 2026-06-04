package com.escrims;

import com.escrims.application.*;
import com.escrims.domain.model.*;
import com.escrims.domain.strategy.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Clase principal que demuestra el funcionamiento del sistema eScrims
 * con todos los patrones de diseño implementados, especialmente los 4 OBLIGATORIOS:
 * 1. STATE PATTERN - Ciclo de vida del Scrim
 * 2. STRATEGY PATTERN - Algoritmos de matchmaking
 * 3. OBSERVER PATTERN - Sistema de eventos
 * 4. FACADE PATTERN - Interface unificada al sistema ⭐
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("=== eScrims: Sistema de Organización de Scrims ===\n");
        
        // ============================================================
        // DEMOSTRACIÓN DEL FACADE PATTERN (Patrón Obligatorio)
        // ============================================================
        System.out.println(">>> USANDO FACADE PATTERN (Interface Unificada) <<<\n");
        
        // Crear facade - punto de entrada único simplificado
        EscrimsFacade facade = new EscrimsFacade();
        
        // Conectar servicios externos
        System.out.println("1. Conectando servicios externos...");
        facade.conectarDiscord();
        facade.conectarSendGrid();
        facade.conectarICal();
        System.out.println("✓ Servicios conectados\n");
        
        // Crear usuarios (Facade encapsula todo)
        System.out.println("2. Creando usuarios a través del Facade...");
        Usuario org = facade.crearUsuario("ProPlayer1", "player1@escrims.com", "hash123", "LATAM");
        Usuario p1 = facade.crearUsuario("ProPlayer2", "player2@escrims.com", "hash456", "LATAM");
        Usuario p2 = facade.crearUsuario("ProPlayer3", "player3@escrims.com", "hash789", "LATAM");
        System.out.println("✓ Usuarios creados: " + org.getUsername() + ", " + 
                          p1.getUsername() + ", " + p2.getUsername() + "\n");
        
        // Crear scrim usando Facade
        System.out.println("3. Creando scrim a través del Facade...");
        Scrim scrim = facade.crearScrim(
            "Valorant", "5v5", "LATAM", org.getId(),
            "Gold", "Platinum", 80,
            LocalDateTime.now().plusHours(2),
            6, "ranked-like"
        );
        System.out.println("✓ Scrim creado: " + scrim.getJuego() + " - Estado: " + 
                          scrim.getNombreEstado() + "\n");
        
        // ============================================================
        // DEMOSTRACIÓN DE PATRONES A TRAVÉS DEL FACADE
        // ============================================================
        
        // STATE PATTERN - Transiciones de estado automáticas
        System.out.println("4. Demostrando STATE PATTERN (a través del Facade)...");
        System.out.println("   Estado inicial: " + scrim.getNombreEstado());
        
        facade.postularseAScrim(scrim.getId(), p1.getId(), "Duelist");
        System.out.println("   Después postulación 1: " + scrim.getNombreEstado());
        
        facade.postularseAScrim(scrim.getId(), p2.getId(), "Controller");
        System.out.println("   Después postulación 2: " + scrim.getNombreEstado());
        
        // Simular más postulaciones para llenar cupos
        for (int i = 3; i <= 6; i++) {
            Usuario u = facade.crearUsuario("Player" + i, "player" + i + "@test.com", "hash", "LATAM");
            facade.postularseAScrim(scrim.getId(), u.getId(), "Support");
        }
        System.out.println("   ✓ Estado después de llenar cupos: " + scrim.getNombreEstado() + "\n");
        
        // Confirmaciones - Demostración REAL del STATE PATTERN
        System.out.println("5. Demostrando CONFIRMACIONES (STATE PATTERN - Transición real)...");
        System.out.println("   Estado ANTES de confirmar: " + scrim.getNombreEstado());
        
        // Confirmar cada participante
        for (Postulacion post : scrim.getPostulaciones()) {
            if (post.getEstado() == EstadoPostulacion.ACEPTADA) {
                try {
                    System.out.println("   → Confirmando: " + post.getUsuario().getUsername());
                    facade.confirmarParticipacion(scrim.getId(), post.getUsuario().getId());
                    System.out.println("     Estado actual: " + scrim.getNombreEstado());
                } catch (IllegalStateException e) {
                    // Ya está en otro estado
                    break;
                }
            }
        }
        System.out.println("   ✓ Estado FINAL después de confirmaciones: " + scrim.getNombreEstado() + "\n");
        
        // Crear segundo scrim para demostración de otros patrones
        System.out.println("6. Creando segundo scrim para demostración de otros patrones...");
        Scrim scrim2 = facade.crearScrim(
            "League of Legends", "5v5", "LATAM", org.getId(),
            "Diamond", "Master", 50,
            LocalDateTime.now().plusHours(3),
            5, "ranked-like"
        );
        System.out.println("   ✓ Nuevo scrim creado: " + scrim2.getJuego() + "\n");
        
        // STRATEGY PATTERN - Cambiar algoritmos en runtime
        System.out.println("7. Demostrando STRATEGY PATTERN...");
        List<Usuario> candidatos = Arrays.asList(p1, p2);
        
        System.out.println("   → Estrategia por MMR:");
        facade.cambiarEstrategiaMatchmaking(new ByMMRStrategy());
        List<Usuario> seleccionados = facade.emparejarJugadores(scrim2.getId(), candidatos);
        System.out.println("     Seleccionados: " + seleccionados.size() + " jugadores");
        
        System.out.println("   → Estrategia por Latencia:");
        facade.cambiarEstrategiaMatchmaking(new ByLatencyStrategy());
        seleccionados = facade.emparejarJugadores(scrim2.getId(), candidatos);
        System.out.println("     Seleccionados: " + seleccionados.size() + " jugadores");
        
        System.out.println("   ✓ Estrategias intercambiables en runtime\n");
        
        // OBSERVER PATTERN - Eventos automáticos
        System.out.println("8. Demostrando OBSERVER PATTERN...");
        System.out.println("   (Los eventos se disparan automáticamente en las operaciones anteriores)");
        System.out.println("   (Las notificaciones fueron enviadas a través del sistema de eventos)\n");
        
        // COMMAND PATTERN - Operaciones reversibles (usando scrim2)
        System.out.println("9. Demostrando patrones adicionales (Command, Chain of Responsibility, Adapter)...");
        facade.asignarRol(scrim2.getId(), p1.getId(), "Duelist");
        System.out.println("   ✓ Rol asignado (reversible con undo)");
        
        facade.enviarMensajeDiscord("escrims", "¡Scrim completado con éxito!");
        System.out.println("   ✓ Mensaje enviado a Discord (Adapter Pattern)\n");
        
        // ============================================================
        // RESUMEN FINAL
        // ============================================================
        System.out.println("=== RESUMEN DE PATRONES IMPLEMENTADOS ===\n");
        System.out.println("PATRONES OBLIGATORIOS (4/4):");
        System.out.println("  ✓ STATE PATTERN - Ciclo de vida del Scrim");
        System.out.println("  ✓ STRATEGY PATTERN - Algoritmos intercambiables");
        System.out.println("  ✓ OBSERVER PATTERN - Sistema de eventos");
        System.out.println("  ✓ FACADE PATTERN - Interface unificada ");
        System.out.println("\nPATRONES ADICIONALES (5 más):");
        System.out.println("  ✓ COMMAND PATTERN - Operaciones reversibles");
        System.out.println("  ✓ CHAIN OF RESPONSIBILITY - Moderación escalonada");
        System.out.println("  ✓ ADAPTER PATTERN - Integraciones externas");
        System.out.println("  ✓ ABSTRACT FACTORY - Notificadores por entorno");
        System.out.println("  ✓ BUILDER PATTERN - Construcción validada");
        System.out.println("\n=== Sistema funcionando correctamente ===");
    }
}
