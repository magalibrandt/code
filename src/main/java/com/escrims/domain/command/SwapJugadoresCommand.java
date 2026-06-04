package com.escrims.domain.command;

import com.escrims.domain.model.Scrim;
import com.escrims.domain.model.Usuario;
import com.escrims.domain.model.Postulacion;
import com.escrims.domain.model.EstadoPostulacion;
import java.util.List;

/**
 * PATRÓN COMMAND - Comando Concreto
 * Comando para intercambiar dos jugadores entre equipos.
 */
public class SwapJugadoresCommand implements ScrimCommand {
    private Usuario usuario1;
    private Usuario usuario2;
    private Postulacion postulacion1Backup;
    private Postulacion postulacion2Backup;
    
    public SwapJugadoresCommand(Usuario usuario1, Usuario usuario2) {
        this.usuario1 = usuario1;
        this.usuario2 = usuario2;
    }
    
    @Override
    public void execute(Scrim scrim) {
        List<Postulacion> postulaciones = scrim.getPostulaciones();
        
        Postulacion post1 = postulaciones.stream()
            .filter(p -> p.getUsuario().equals(usuario1))
            .findFirst()
            .orElse(null);
            
        Postulacion post2 = postulaciones.stream()
            .filter(p -> p.getUsuario().equals(usuario2))
            .findFirst()
            .orElse(null);
        
        if (post1 != null && post2 != null) {
            // Guardar estado anterior para undo
            this.postulacion1Backup = new Postulacion(post1);
            this.postulacion2Backup = new Postulacion(post2);
            
            // Intercambiar roles
            String rolTemp = post1.getRolDeseado();
            post1.setRolDeseado(post2.getRolDeseado());
            post2.setRolDeseado(rolTemp);
        }
    }
    
    @Override
    public void undo(Scrim scrim) {
        if (postulacion1Backup != null && postulacion2Backup != null) {
            List<Postulacion> postulaciones = scrim.getPostulaciones();
            
            Postulacion post1 = postulaciones.stream()
                .filter(p -> p.getUsuario().equals(usuario1))
                .findFirst()
                .orElse(null);
                
            Postulacion post2 = postulaciones.stream()
                .filter(p -> p.getUsuario().equals(usuario2))
                .findFirst()
                .orElse(null);
            
            if (post1 != null && post2 != null) {
                post1.setRolDeseado(postulacion1Backup.getRolDeseado());
                post2.setRolDeseado(postulacion2Backup.getRolDeseado());
            }
        }
    }
    
    @Override
    public String getDescription() {
        return String.format("Intercambiar roles entre '%s' y '%s'", 
            usuario1.getUsername(), usuario2.getUsername());
    }
}
