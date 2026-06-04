package com.escrims.tests;

import com.escrims.domain.model.*;
import com.escrims.domain.strategy.*;
import com.escrims.domain.command.*;
import com.escrims.infrastructure.notifications.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Ejemplos de Unit Tests para los patrones de diseño.
 * En un proyecto real, usar JUnit 4/5 y Mockito.
 */
public class PatternTests {
    
    /**
     * TEST: Strategy Pattern - ByMMRStrategy
     */
    public static void testByMMRStrategy() {
        System.out.println("\n=== TEST: ByMMRStrategy ===");
        
        Usuario u1 = new Usuario("Player1", "p1@test.com", "hash1", "LATAM");
        u1.agregarRango("Valorant", "Gold");
        
        Usuario u2 = new Usuario("Player2", "p2@test.com", "hash2", "LATAM");
        u2.agregarRango("Valorant", "Platinum");
        
        Usuario u3 = new Usuario("Player3", "p3@test.com", "hash3", "LATAM");
        u3.agregarRango("Valorant", "Silver");
        
        Scrim scrim = new Scrim("Valorant", "5v5", "LATAM", u1);
        scrim.setRangoMin("Gold");
        scrim.setRangoMax("Platinum");
        
        MatchmakingStrategy strategy = new ByMMRStrategy();
        List<Usuario> candidatos = Arrays.asList(u1, u2, u3);
        List<Usuario> seleccionados = strategy.seleccionar(candidatos, scrim);
        
        // Assertions
        assert seleccionados.size() >= 1 : "Debe seleccionar al menos 1 jugador";
        assert seleccionados.contains(u1) : "Debe contener Gold (u1)";
        assert seleccionados.contains(u2) : "Debe contener Platinum (u2)";
        System.out.println("✓ Test pasado: " + seleccionados.size() + " jugadores seleccionados");
    }
    
    /**
     * TEST: State Pattern - Transiciones de estado
     */
    public static void testStateTransitions() {
        System.out.println("\n=== TEST: State Pattern Transitions ===");
        
        Usuario organizador = new Usuario("Org", "org@test.com", "hash", "LATAM");
        Scrim scrim = new Scrim("Valorant", "5v5", "LATAM", organizador);
        
        // Estado inicial
        assert scrim.getNombreEstado().equals("Buscando Jugadores") : 
            "Estado inicial debe ser Buscando Jugadores";
        System.out.println("✓ Estado inicial: " + scrim.getNombreEstado());
        
        // Simular cupo completo
        for (int i = 1; i <= 5; i++) {
            Usuario u = new Usuario("Player" + i, "p" + i + "@test.com", "hash" + i, "LATAM");
            u.agregarRango("Valorant", "Gold");
            scrim.postular(u, "Duelist");
        }
        
        // Verificar transición
        assert scrim.getNombreEstado().equals("Lobby Armado") : 
            "Debe transicionar a Lobby Armado cuando cupo completo";
        System.out.println("✓ Transición a: " + scrim.getNombreEstado());
        
        // Confirmar participación
        for (Postulacion p : scrim.getPostulaciones()) {
            if (p.getEstado() == EstadoPostulacion.ACEPTADA) {
                scrim.confirmar(p.getUsuario());
            }
        }
        
        assert scrim.getNombreEstado().equals("Confirmado") : 
            "Debe transicionar a Confirmado cuando todos confirman";
        System.out.println("✓ Transición a: " + scrim.getNombreEstado());
        
        // Iniciar
        scrim.iniciar();
        assert scrim.getNombreEstado().equals("En Juego") : 
            "Debe transicionar a En Juego";
        System.out.println("✓ Transición a: " + scrim.getNombreEstado());
        
        // Finalizar
        scrim.finalizar();
        assert scrim.getNombreEstado().equals("Finalizado") : 
            "Debe transicionar a Finalizado";
        System.out.println("✓ Transición a: " + scrim.getNombreEstado());
    }
    
    /**
     * TEST: Command Pattern - Undo/Redo
     */
    public static void testCommandPattern() {
        System.out.println("\n=== TEST: Command Pattern ===");
        
        Usuario organizador = new Usuario("Org", "org@test.com", "hash", "LATAM");
        Usuario jugador = new Usuario("Player", "p@test.com", "hash", "LATAM");
        
        Scrim scrim = new Scrim("Valorant", "5v5", "LATAM", organizador);
        CommandInvoker invoker = new CommandInvoker(scrim);
        
        // Rol inicial
        String rolInicial = jugador.getRolesPreferidos().isEmpty() ? 
            "NINGUNO" : jugador.getRolesPreferidos().get(0);
        System.out.println("Rol inicial: " + rolInicial);
        
        // Ejecutar comando
        invoker.ejecutar(new AsignarRolCommand(jugador, "Duelist"));
        assert jugador.getRolesPreferidos().contains("Duelist") : 
            "Debe tener rol Duelist";
        System.out.println("✓ Rol asignado: " + jugador.getRolesPreferidos().get(0));
        
        // Deshacer
        invoker.deshacer();
        System.out.println("✓ Comando deshecho");
        
        // Verificar historial
        List<String> historial = invoker.obtenerHistorial();
        assert historial.size() >= 1 : "Debe haber historial";
        System.out.println("✓ Historial registrado: " + historial.size() + " comando(s)");
    }
    
    /**
     * TEST: Builder Pattern - Validaciones
     */
    public static void testBuilderPattern() {
        System.out.println("\n=== TEST: Builder Pattern ===");
        
        Usuario organizador = new Usuario("Org", "org@test.com", "hash", "LATAM");
        
        try {
            Scrim scrim = new ScrimBuilder("Valorant", "5v5", "LATAM", organizador)
                .conRangos("Gold", "Platinum")
                .conLatenciaMaxima(80)
                .conFechaHora(LocalDateTime.now().plusHours(2))
                .conCupos(10)
                .conModalidad("ranked-like")
                .build();
            
            assert scrim != null : "Scrim debe ser creado";
            assert scrim.getRangoMin().equals("Gold") : "Rango mínimo debe ser Gold";
            assert scrim.getCuposTotales() == 10 : "Cupos deben ser 10";
            
            System.out.println("✓ Scrim construido exitosamente");
            System.out.println("  Juego: " + scrim.getJuego());
            System.out.println("  Rango: " + scrim.getRangoMin() + " - " + scrim.getRangoMax());
            System.out.println("  Cupos: " + scrim.getCuposTotales());
        } catch (Exception e) {
            System.out.println("✗ Test fallido: " + e.getMessage());
        }
    }
    
    /**
     * TEST: Abstract Factory Pattern
     */
    public static void testAbstractFactory() {
        System.out.println("\n=== TEST: Abstract Factory Pattern ===");
        
        // Dev Environment
        NotifierFactory devFactory = new DevNotifierFactory();
        Notifier pushDev = devFactory.createPush();
        System.out.println("✓ Dev Push Notifier: " + pushDev.getChannelName());
        
        // Prod Environment
        NotifierFactory prodFactory = new ProdNotifierFactory();
        Notifier pushProd = prodFactory.createPush();
        System.out.println("✓ Prod Push Notifier: " + pushProd.getChannelName());
        
        assert !pushDev.getClass().equals(pushProd.getClass()) : 
            "Dev y Prod deben usar notificadores diferentes";
        System.out.println("✓ Diferentes implementaciones por entorno");
    }
    
    /**
     * TEST: Validación de Usuario
     */
    public static void testUsuarioValidation() {
        System.out.println("\n=== TEST: Usuario Validation ===");
        
        Usuario usuario = new Usuario("TestPlayer", "test@test.com", "hash", "LATAM");
        
        // Agregar rangos
        usuario.agregarRango("Valorant", "Gold");
        usuario.agregarRango("LoL", "Silver");
        
        assert usuario.getRangoParaJuego("Valorant").equals("Gold") : 
            "Rango debe ser Gold";
        assert usuario.getRangoParaJuego("CS2").equals("Unranked") : 
            "Rango no asignado debe ser Unranked";
        
        System.out.println("✓ Usuario con rangos:");
        System.out.println("  Valorant: " + usuario.getRangoParaJuego("Valorant"));
        System.out.println("  LoL: " + usuario.getRangoParaJuego("LoL"));
        System.out.println("  CS2: " + usuario.getRangoParaJuego("CS2"));
        
        // Agregar roles
        usuario.agregarRolPreferido("Duelist");
        usuario.agregarRolPreferido("Support");
        
        assert usuario.getRolesPreferidos().size() == 2 : 
            "Debe tener 2 roles preferidos";
        System.out.println("✓ Roles preferidos: " + usuario.getRolesPreferidos());
    }
    
    /**
     * TEST: Sistema de Strikes
     */
    public static void testStrikeSystem() {
        System.out.println("\n=== TEST: Strike System ===");
        
        Usuario usuario = new Usuario("Player", "p@test.com", "hash", "LATAM");
        
        assert usuario.getStrikes() == 0 : "Strikes iniciales debe ser 0";
        assert !usuario.estaBajoSancion() : "No debe estar sancionado";
        
        usuario.aplicarStrike();
        assert usuario.getStrikes() == 1 : "Strikes debe ser 1";
        System.out.println("✓ Strike 1 aplicado");
        
        usuario.aplicarStrike();
        usuario.aplicarStrike();
        assert usuario.getStrikes() == 3 : "Strikes debe ser 3";
        assert usuario.estaBajoSancion() : "Debe estar bajo sanción con 3 strikes";
        System.out.println("✓ Strikes: " + usuario.getStrikes());
        System.out.println("✓ Cooldown activo hasta: " + usuario.getCooldownHasta());
    }
    
    /**
     * Ejecutar todos los tests
     */
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║           TESTS DE PATRONES DE DISEÑO - eSCRIMS           ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        
        try {
            testByMMRStrategy();
            testStateTransitions();
            testCommandPattern();
            testBuilderPattern();
            testAbstractFactory();
            testUsuarioValidation();
            testStrikeSystem();
            
            System.out.println("\n╔════════════════════════════════════════════════════════════╗");
            System.out.println("║                    ✓ TODOS LOS TESTS PASARON             ║");
            System.out.println("╚════════════════════════════════════════════════════════════╝\n");
        } catch (AssertionError e) {
            System.out.println("\n✗ TEST FALLIDO: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
