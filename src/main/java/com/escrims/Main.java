package com.escrims;

import com.escrims.application.facade.EscrimsFacade;
import com.escrims.domain.model.Postulacion;
import com.escrims.domain.model.Scrim;
import com.escrims.domain.model.Usuario;
import com.escrims.domain.strategy.ByLatencyStrategy;
import com.escrims.domain.strategy.ByMMRStrategy;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== eScrims: Sistema de Organizacion de Scrims ===\n");
        System.out.println(">>> FACADE: interfaz unificada de aplicacion <<<\n");

        EscrimsFacade facade = new EscrimsFacade(new ByMMRStrategy());

        Usuario organizador = facade.crearUsuario("Organizador", "org@escrims.com", "hash", "LATAM");
        Usuario jugador1 = facade.crearUsuario("ProPlayer1", "p1@escrims.com", "hash", "LATAM");
        Usuario jugador2 = facade.crearUsuario("ProPlayer2", "p2@escrims.com", "hash", "LATAM");
        jugador1.agregarRango("Valorant", "Gold");
        jugador2.agregarRango("Valorant", "Platinum");

        Scrim scrim = facade.crearScrim(
            "Valorant", "5v5", "LATAM", organizador.getId(),
            "Gold", "Platinum", 80,
            LocalDateTime.now().plusHours(2),
            4, "ranked-like"
        );

        System.out.println("Scrim creado: " + scrim.getJuego() + " - Estado: " + scrim.getNombreEstado());

        System.out.println("\n>>> STATE: postulaciones y transiciones <<<");
        facade.postularseAScrim(scrim.getId(), jugador1.getId(), "Duelist");
        facade.postularseAScrim(scrim.getId(), jugador2.getId(), "Controller");
        for (int i = 3; i <= 4; i++) {
            Usuario usuario = facade.crearUsuario("Player" + i, "player" + i + "@test.com", "hash", "LATAM");
            usuario.agregarRango("Valorant", "Gold");
            facade.postularseAScrim(scrim.getId(), usuario.getId(), "Support");
        }
        System.out.println("Estado luego de completar cupos: " + scrim.getNombreEstado());

        for (Postulacion postulacion : scrim.getPostulaciones()) {
            if (postulacion.getEstado().esAceptada()) {
                facade.confirmarParticipacion(scrim.getId(), postulacion.getUsuario().getId());
            }
        }
        System.out.println("Estado luego de confirmaciones: " + scrim.getNombreEstado());

        facade.iniciarScrim(scrim.getId());
        System.out.println("Estado luego de iniciar: " + scrim.getNombreEstado());

        System.out.println("\n>>> STRATEGY: cambio de algoritmo <<<");
        List<Usuario> candidatos = List.of(jugador1, jugador2);
        facade.cambiarEstrategiaMatchmaking(new ByMMRStrategy());
        System.out.println("Seleccionados por MMR: " + facade.emparejarJugadores(scrim.getId(), candidatos).size());
        facade.cambiarEstrategiaMatchmaking(new ByLatencyStrategy());
        System.out.println("Seleccionados por latencia: " + facade.emparejarJugadores(scrim.getId(), candidatos).size());

        System.out.println("\n>>> OBSERVER: notificaciones por EventBus <<<");
        System.out.println("Los eventos de cambio de estado fueron publicados automaticamente por los estados concretos.");
        System.out.println("NotificationSubscriber recibio esos eventos y envio notificaciones mediante NotifierFactory.");

        System.out.println("\n=== Demo principal finalizada ===");
    }
}
