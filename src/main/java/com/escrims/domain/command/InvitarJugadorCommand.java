package com.escrims.domain.command;

import com.escrims.domain.model.Scrim;
import com.escrims.domain.model.Usuario;

/**
 * PATRÓN COMMAND - Comando Concreto
 * Comando para invitar a un usuario específico a un Scrim.
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
        // Verificar si ya estaba postulado
        boolean yaExiste = scrim.getPostulaciones().stream()
            .anyMatch(p -> p.getUsuario().equals(usuarioInvitado));
        
        this.yaEstabaInvitado = yaExiste;
        
        if (!yaExiste) {
            // Enviar invitación (en implementación real, se enviaría una notificación)
            // Por ahora solo registramos la postulación
            scrim.postular(usuarioInvitado, rolSugerido);
        }
    }
    
    @Override
    public void undo(Scrim scrim) {
        if (!yaEstabaInvitado) {
            // Remover la postulación creada
            scrim.getPostulaciones().removeIf(p -> p.getUsuario().equals(usuarioInvitado));
        }
    }
    
    @Override
    public String getDescription() {
        return String.format("Invitar a '%s' para rol '%s'", usuarioInvitado.getUsername(), rolSugerido);
    }
}
