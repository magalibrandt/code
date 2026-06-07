package com.escrims.tests;

import com.escrims.application.builder.ScrimBuilder;
import com.escrims.domain.command.AsignarRolCommand;
import com.escrims.domain.command.CommandInvoker;
import com.escrims.domain.model.Postulacion;
import com.escrims.domain.model.Scrim;
import com.escrims.domain.model.Usuario;
import com.escrims.domain.strategy.ByMMRStrategy;
import com.escrims.domain.strategy.MatchmakingStrategy;
import com.escrims.infrastructure.notifications.DevNotifierFactory;
import com.escrims.infrastructure.notifications.Notifier;
import com.escrims.infrastructure.notifications.NotifierFactory;
import com.escrims.infrastructure.notifications.ProdNotifierFactory;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class PatternTests {

    public static void testByMMRStrategy() {
        Usuario u1 = new Usuario("Player1", "p1@test.com", "hash1", "LATAM");
        u1.agregarRango("Valorant", "Gold");

        Usuario u2 = new Usuario("Player2", "p2@test.com", "hash2", "LATAM");
        u2.agregarRango("Valorant", "Platinum");

        Usuario u3 = new Usuario("Player3", "p3@test.com", "hash3", "LATAM");
        u3.agregarRango("Valorant", "Silver");

        Scrim scrim = new Scrim("Valorant", "5v5", "LATAM", u1);
        scrim.setRangoMin("Gold");
        scrim.setRangoMax("Platinum");
        scrim.setCuposTotales(2);

        MatchmakingStrategy strategy = new ByMMRStrategy();
        List<Usuario> seleccionados = strategy.seleccionar(Arrays.asList(u1, u2, u3), scrim);

        require(seleccionados.size() == 2, "Debe seleccionar 2 jugadores por cupo");
        require(seleccionados.contains(u1), "Debe contener Gold");
        require(seleccionados.contains(u2), "Debe contener Platinum");
    }

    public static void testStateTransitions() {
        Usuario organizador = new Usuario("Org", "org@test.com", "hash", "LATAM");
        Scrim scrim = new Scrim("Valorant", "5v5", "LATAM", organizador);
        scrim.setCuposTotales(5);

        require(scrim.getNombreEstado().equals("Buscando Jugadores"), "Estado inicial incorrecto");

        for (int i = 1; i <= 5; i++) {
            Usuario u = new Usuario("Player" + i, "p" + i + "@test.com", "hash" + i, "LATAM");
            u.agregarRango("Valorant", "Gold");
            scrim.postular(u, "Duelist");
        }

        require(scrim.getNombreEstado().equals("Lobby Armado"), "Debe pasar a Lobby Armado");

        for (Postulacion p : scrim.getPostulaciones()) {
            if (p.getEstado().esAceptada()) {
                scrim.confirmar(p.getUsuario());
            }
        }

        require(scrim.getNombreEstado().equals("Confirmado"), "Debe pasar a Confirmado");

        scrim.iniciar();
        require(scrim.getNombreEstado().equals("En Juego"), "Debe pasar a En Juego");

        scrim.finalizar();
        require(scrim.getNombreEstado().equals("Finalizado"), "Debe pasar a Finalizado");
    }

    public static void testCommandPattern() {
        Usuario organizador = new Usuario("Org", "org@test.com", "hash", "LATAM");
        Usuario jugador = new Usuario("Player", "p@test.com", "hash", "LATAM");

        Scrim scrim = new Scrim("Valorant", "5v5", "LATAM", organizador);
        scrim.getEquipoA().agregarJugador(jugador, "Support");

        CommandInvoker invoker = new CommandInvoker(scrim);
        invoker.ejecutar(new AsignarRolCommand(jugador, "Duelist"));
        require("Duelist".equals(scrim.getEquipoA().getRolDeJugador(jugador)), "Debe asignar rol en Equipo");

        invoker.deshacer();
        require("Support".equals(scrim.getEquipoA().getRolDeJugador(jugador)), "Undo debe restaurar rol anterior");
    }

    public static void testBuilderPattern() {
        Usuario organizador = new Usuario("Org", "org@test.com", "hash", "LATAM");

        Scrim scrim = new ScrimBuilder("Valorant", "5v5", "LATAM", organizador)
            .conRangos("Gold", "Platinum")
            .conLatenciaMaxima(80)
            .conFechaHora(LocalDateTime.now().plusHours(2))
            .conCupos(10)
            .conModalidad("ranked-like")
            .build();

        require(scrim.getRangoMin().equals("Gold"), "Rango minimo debe ser Gold");
        require(scrim.getCuposTotales() == 10, "Cupos deben ser 10");
    }

    public static void testAbstractFactory() {
        NotifierFactory devFactory = new DevNotifierFactory();
        Notifier pushDev = devFactory.createPushNotifier();

        NotifierFactory prodFactory = new ProdNotifierFactory();
        Notifier pushProd = prodFactory.createPushNotifier();

        require(!pushDev.getClass().equals(pushProd.getClass()), "Dev y Prod deben usar implementaciones diferentes");
    }

    public static void testUsuarioValidation() {
        Usuario usuario = new Usuario("TestPlayer", "test@test.com", "hash", "LATAM");
        usuario.agregarRango("Valorant", "Gold");
        usuario.agregarRango("LoL", "Silver");

        require(usuario.getRangoParaJuego("Valorant").equals("Gold"), "Rango Valorant incorrecto");
        require(usuario.getRangoParaJuego("CS2").equals("Unranked"), "Rango default incorrecto");

        usuario.agregarRolPreferido("Duelist");
        usuario.agregarRolPreferido("Support");
        require(usuario.getRolesPreferidos().size() == 2, "Debe tener 2 roles preferidos");
    }

    public static void testStrikeSystem() {
        Usuario usuario = new Usuario("Player", "p@test.com", "hash", "LATAM");

        require(usuario.getStrikes() == 0, "Strikes iniciales debe ser 0");
        require(!usuario.estaBajoSancion(), "No debe estar sancionado inicialmente");

        usuario.aplicarStrike();
        require(usuario.getStrikes() == 1, "Strikes debe ser 1");

        usuario.aplicarStrike();
        usuario.aplicarStrike();
        require(usuario.getStrikes() == 3, "Strikes debe ser 3");
        require(usuario.estaBajoSancion(), "Debe estar bajo sancion con 3 strikes");
    }

    public static void main(String[] args) {
        testByMMRStrategy();
        testStateTransitions();
        testCommandPattern();
        testBuilderPattern();
        testAbstractFactory();
        testUsuarioValidation();
        testStrikeSystem();
        System.out.println("TODOS LOS TESTS PASARON");
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
