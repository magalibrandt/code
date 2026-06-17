package com.escrims;

import com.escrims.application.facade.EscrimsFacade;
import com.escrims.domain.model.Postulacion;
import com.escrims.domain.model.Scrim;
import com.escrims.domain.model.Usuario;
import com.escrims.domain.strategy.ByLatencyStrategy;
import com.escrims.domain.strategy.ByMMRStrategy;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class EjemploConFacade {
    public static void main(String[] args) {
        System.out.println("=== Ejemplo limpio de Facade ===\n");

        EscrimsFacade facade = new EscrimsFacade(new ByMMRStrategy());

        Usuario organizador = facade.crearUsuario("Organizador", "org@escrims.com", "password123", "LATAM");
        Usuario jugador1 = facade.crearUsuario("ProGamer", "pro@escrims.com", "password456", "LATAM");
        Usuario jugador2 = facade.crearUsuario("Support", "sup@escrims.com", "password789", "LATAM");
        jugador1.agregarRango("Valorant", "Gold");
        jugador2.agregarRango("Valorant", "Platinum");

        Scrim scrim = facade.crearScrim(
            "Valorant", "5v5", "LATAM",
            organizador.getId(),
            "Gold", "Platinum", 80,
            LocalDateTime.now().plusHours(2),
            4, "ranked-like"
        );

        List<Scrim> disponibles = facade.buscarScrims("Valorant", "5v5", "LATAM", "Gold", "Platinum", 80);
        System.out.println("Scrims encontrados: " + disponibles.size());

        facade.postularseAScrim(scrim.getId(), jugador1.getId(), "Duelist");
        facade.postularseAScrim(scrim.getId(), jugador2.getId(), "Support");
        for (int i = 3; i <= 4; i++) {
            Usuario usuario = facade.crearUsuario("Player" + i, "p" + i + "@test.com", "pass" + i, "LATAM");
            usuario.agregarRango("Valorant", "Gold");
            facade.postularseAScrim(scrim.getId(), usuario.getId(), "Duelist");
        }

        for (Postulacion postulacion : scrim.getPostulaciones()) {
            if (postulacion.getEstado().esAceptada()) {
                facade.confirmarParticipacion(scrim.getId(), postulacion.getUsuario().getId());
            }
        }

        facade.cambiarEstrategiaMatchmaking(new ByLatencyStrategy());
        System.out.println("Candidatos por nueva estrategia: "
            + facade.emparejarJugadores(scrim.getId(), List.of(jugador1, jugador2)).size());

        facade.asignarRol(scrim.getId(), jugador1.getId(), "Duelist");
        Usuario jugadorEquipoB = scrim.getEquipoB().getJugadores().get(0);
        facade.intercambiarJugadores(scrim.getId(), jugador1.getId(), jugadorEquipoB.getId());

        facade.iniciarScrim(scrim.getId());
        facade.finalizarScrim(scrim.getId());

        facade.reportarConductaInapropiada(scrim.getId(), jugador1.getId(),
            jugador2.getId(), "LENGUAJE_OFENSIVO", "Comportamiento inapropiado");

        Map<String, Object> infoScrim = facade.obtenerInfoScrim(scrim.getId());
        System.out.println("Estado final del scrim: " + infoScrim.get("estado"));
        System.out.println("Las notificaciones se canalizan por Observer/EventBus + NotificationSubscriber + NotifierFactory.");
    }
}
