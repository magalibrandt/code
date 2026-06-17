package com.escrims.domain.state;

import com.escrims.domain.model.*;
import com.escrims.domain.events.*;

/**
 * PATRÓN STATE - Estado Concreto
 * Representa el estado donde el lobby está completo y esperando confirmaciones.
 */
public class LobbyArmadoState implements ScrimState {
    
    @Override
    public void postular(Scrim scrim, Usuario usuario, String rolDeseado) {
        throw new IllegalStateException("El lobby ya está completo");
    }
    
    @Override
    public void confirmar(Scrim scrim, Usuario usuario) {
        // Verificar que el usuario está en el scrim
        boolean estaEnScrim = scrim.getPostulaciones().stream()
            .anyMatch(p -> p.getUsuario().equals(usuario) && 
                          p.getEstado().esAceptada());
        
        if (!estaEnScrim) {
            throw new IllegalStateException("Usuario no está en este scrim");
        }
        
        // Crear confirmación
        Confirmacion confirmacion = new Confirmacion(usuario, scrim);
        confirmacion.confirmar();
        scrim.agregarConfirmacion(confirmacion);
        
        // Si todos confirmaron, cambiar a Confirmado
        if (scrim.todosConfirmaron()) {
            scrim.setEstado(new ConfirmadoState());
            DomainEventBus.getInstance().publish(
                new ScrimStateChangedEvent(scrim.getId(), "Lobby Armado", "Confirmado")
            );
        }
    }
    
    @Override
    public void iniciar(Scrim scrim) {
        throw new IllegalStateException("Debe estar confirmado antes de iniciar");
    }
    
    @Override
    public void finalizar(Scrim scrim) {
        throw new IllegalStateException("No se puede finalizar un scrim que no ha iniciado");
    }
    
    @Override
    public void cancelar(Scrim scrim) {
        scrim.setEstado(new CanceladoState());
        DomainEventBus.getInstance().publish(
            new ScrimStateChangedEvent(scrim.getId(), "Lobby Armado", "Cancelado")
        );
    }
    
    @Override
    public String getNombre() {
        return "Lobby Armado";
    }
}
