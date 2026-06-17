package com.escrims.domain.command;

import com.escrims.domain.model.Equipo;
import com.escrims.domain.model.Scrim;
import com.escrims.domain.model.Usuario;

/**
 * PATRON COMMAND - Comando Concreto.
 * Intercambia dos jugadores entre equipo A y equipo B.
 */
public class SwapJugadoresCommand implements ScrimCommand {
    private Usuario usuario1;
    private Usuario usuario2;
    private Equipo equipo1Original;
    private Equipo equipo2Original;
    private String rol1Original;
    private String rol2Original;

    public SwapJugadoresCommand(Usuario usuario1, Usuario usuario2) {
        this.usuario1 = usuario1;
        this.usuario2 = usuario2;
    }

    @Override
    public void execute(Scrim scrim) {
        Equipo equipo1 = buscarEquipo(scrim, usuario1);
        Equipo equipo2 = buscarEquipo(scrim, usuario2);
        if (equipo1 == null || equipo2 == null) {
            throw new IllegalStateException("Ambos usuarios deben pertenecer a equipos del scrim");
        }
        if (equipo1 == equipo2) {
            throw new IllegalStateException("Los usuarios ya estan en el mismo equipo");
        }

        if (equipo1Original == null && equipo2Original == null) {
            equipo1Original = equipo1;
            equipo2Original = equipo2;
            rol1Original = equipo1.getRolDeJugador(usuario1);
            rol2Original = equipo2.getRolDeJugador(usuario2);
        }

        equipo1.removerJugador(usuario1);
        equipo2.removerJugador(usuario2);
        equipo1.agregarJugador(usuario2, rol2Original);
        equipo2.agregarJugador(usuario1, rol1Original);
    }

    @Override
    public void undo(Scrim scrim) {
        if (equipo1Original != null && equipo2Original != null) {
            Equipo equipoActual1 = buscarEquipo(scrim, usuario1);
            Equipo equipoActual2 = buscarEquipo(scrim, usuario2);
            if (equipoActual1 != null) {
                equipoActual1.removerJugador(usuario1);
            }
            if (equipoActual2 != null) {
                equipoActual2.removerJugador(usuario2);
            }
            equipo1Original.agregarJugador(usuario1, rol1Original);
            equipo2Original.agregarJugador(usuario2, rol2Original);
        }
    }

    @Override
    public String getDescription() {
        return String.format("Intercambiar jugadores '%s' y '%s'",
            usuario1.getUsername(), usuario2.getUsername());
    }

    private Equipo buscarEquipo(Scrim scrim, Usuario usuario) {
        if (scrim.getEquipoA().contieneJugador(usuario)) {
            return scrim.getEquipoA();
        }
        if (scrim.getEquipoB().contieneJugador(usuario)) {
            return scrim.getEquipoB();
        }
        return null;
    }
}
