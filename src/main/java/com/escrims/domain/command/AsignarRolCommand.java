package com.escrims.domain.command;

import com.escrims.domain.model.Scrim;
import com.escrims.domain.model.Usuario;
import com.escrims.domain.model.Equipo;

/**
 * PATRÓN COMMAND - Comando Concreto
 * Comando para asignar un rol a un usuario dentro del Scrim.
 */
public class AsignarRolCommand implements ScrimCommand {
    private Usuario usuario;
    private String nuevoRol;
    private String rolAnterior;
    private Equipo equipo;
    
    public AsignarRolCommand(Usuario usuario, String nuevoRol) {
        this.usuario = usuario;
        this.nuevoRol = nuevoRol;
        this.rolAnterior = null;
    }
    
    @Override
    public void execute(Scrim scrim) {
        this.equipo = buscarEquipoDelUsuario(scrim);
        if (equipo == null) {
            throw new IllegalStateException("El usuario no pertenece a ningun equipo del scrim");
        }
        this.rolAnterior = equipo.getRolDeJugador(usuario);
        equipo.asignarRol(usuario, nuevoRol);
    }
    
    @Override
    public void undo(Scrim scrim) {
        Equipo equipoActual = equipo != null ? equipo : buscarEquipoDelUsuario(scrim);
        if (equipoActual != null) {
            equipoActual.asignarRol(usuario, rolAnterior);
        }
    }
    
    @Override
    public String getDescription() {
        return String.format("Asignar rol '%s' a usuario '%s'", nuevoRol, usuario.getUsername());
    }
    
    private Equipo buscarEquipoDelUsuario(Scrim scrim) {
        if (scrim.getEquipoA().contieneJugador(usuario)) {
            return scrim.getEquipoA();
        }
        if (scrim.getEquipoB().contieneJugador(usuario)) {
            return scrim.getEquipoB();
        }
        return null;
    }
}
