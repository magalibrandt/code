package com.escrims.domain.state;

import com.escrims.domain.model.*;
import com.escrims.domain.events.*;

/**
 * PATRÓN STATE - Estado Concreto
 * Representa el estado inicial donde el scrim está buscando jugadores.
 */
public class BuscandoJugadoresState implements ScrimState {
    
    @Override
    public void postular(Scrim scrim, Usuario usuario, String rolDeseado) {
        // Validar que el usuario cumple requisitos
        if (usuario.estaBajoSancion()) {
            throw new IllegalStateException("Usuario bajo sanción no puede postularse");
        }
        
        // Crear postulación
        Postulacion postulacion = new Postulacion(usuario, scrim, rolDeseado);
        postulacion.aceptar(); // Auto-aceptar en este estado
        scrim.agregarPostulacion(postulacion);
        
        // Publicar evento
        DomainEventBus.getInstance().publish(
            new PostulacionAceptadaEvent(scrim.getId(), usuario.getId())
        );
        
        // Si se completó el cupo, cambiar a LobbyArmado
        if (scrim.estaCompleto()) {
            scrim.setEstado(new LobbyArmadoState());
            DomainEventBus.getInstance().publish(
                new ScrimStateChangedEvent(scrim.getId(), "LobbyArmado")
            );
        }
    }
    
    @Override
    public void confirmar(Scrim scrim, Usuario usuario) {
        throw new IllegalStateException("No se puede confirmar en estado Buscando Jugadores");
    }
    
    @Override
    public void iniciar(Scrim scrim) {
        throw new IllegalStateException("No se puede iniciar sin completar el lobby");
    }
    
    @Override
    public void finalizar(Scrim scrim) {
        throw new IllegalStateException("No se puede finalizar un scrim que no ha iniciado");
    }
    
    @Override
    public void cancelar(Scrim scrim) {
        scrim.setEstado(new CanceladoState());
        DomainEventBus.getInstance().publish(
            new ScrimStateChangedEvent(scrim.getId(), "Cancelado")
        );
    }
    
    @Override
    public String getNombreEstado() {
        return "Buscando Jugadores";
    }
}
