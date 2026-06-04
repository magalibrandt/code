package com.escrims.domain.command;

import com.escrims.domain.model.Scrim;
import com.escrims.domain.model.Usuario;

/**
 * PATRÓN COMMAND - Comando Concreto
 * Comando para asignar un rol a un usuario dentro del Scrim.
 */
public class AsignarRolCommand implements ScrimCommand {
    private Usuario usuario;
    private String nuevoRol;
    private String rolAnterior;
    
    public AsignarRolCommand(Usuario usuario, String nuevoRol) {
        this.usuario = usuario;
        this.nuevoRol = nuevoRol;
        this.rolAnterior = null;
    }
    
    @Override
    public void execute(Scrim scrim) {
        // Guardar rol anterior para poder hacer undo
        this.rolAnterior = usuario.getRolesPreferidos().isEmpty() 
            ? null 
            : usuario.getRolesPreferidos().get(0);
        
        // Asignar nuevo rol
        usuario.agregarRolPreferido(nuevoRol);
    }
    
    @Override
    public void undo(Scrim scrim) {
        if (rolAnterior != null) {
            usuario.agregarRolPreferido(rolAnterior);
        }
    }
    
    @Override
    public String getDescription() {
        return String.format("Asignar rol '%s' a usuario '%s'", nuevoRol, usuario.getUsername());
    }
}
