package com.escrims.domain.command;

import com.escrims.domain.model.Scrim;
import com.escrims.domain.model.Usuario;

/**
 * PATRON COMMAND - Comando Concreto.
 * Invitacion simplificada: registra una postulacion mediante el flujo de dominio.
 */
public class InvitarJugadorCommand implements ScrimCommand {
    private Usuario usuarioInvitado;
    private String rolSugerido;
    private boolean yaEstabaInvitado;

    public InvitarJugadorCommand(Usuario usuarioInvitado, String rolSugerido) {
        this.usuarioInvitado = usuarioInvitado;
        this.rolSugerido = rolSugerido;
        this.yaEstabaInvitado = false;
    }

    @Override
    public void execute(Scrim scrim) {
        boolean yaExiste = scrim.getPostulaciones().stream()
            .anyMatch(p -> p.getUsuario().equals(usuarioInvitado));

        this.yaEstabaInvitado = yaExiste;

        if (!yaExiste) {
            scrim.postular(usuarioInvitado, rolSugerido);
        }
    }

    @Override
    public void undo(Scrim scrim) {
        if (!yaEstabaInvitado) {
            scrim.removerPostulacionDe(usuarioInvitado);
        }
    }

    @Override
    public String getDescription() {
        return String.format("Invitar a '%s' para rol '%s'", usuarioInvitado.getUsername(), rolSugerido);
    }
}
