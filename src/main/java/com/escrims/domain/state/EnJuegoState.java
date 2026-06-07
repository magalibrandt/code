package com.escrims.domain.state;

import com.escrims.domain.model.*;
import com.escrims.domain.events.*;

/**
 * PATRÓN STATE - Estado Concreto
 * Representa el estado donde el scrim está en curso.
 */
public class EnJuegoState implements ScrimState {
    
    @Override
    public void postular(Scrim scrim, Usuario usuario, String rolDeseado) {
        throw new IllegalStateException("El scrim ya está en juego");
    }
    
    @Override
    public void confirmar(Scrim scrim, Usuario usuario) {
        throw new IllegalStateException("El scrim ya está en juego");
    }
    
    @Override
    public void iniciar(Scrim scrim) {
        throw new IllegalStateException("El scrim ya está iniciado");
    }
    
    @Override
    public void finalizar(Scrim scrim) {
        scrim.setEstado(new FinalizadoState());
        DomainEventBus.getInstance().publish(
            new ScrimStateChangedEvent(scrim.getId(), "En Juego", "Finalizado")
        );
    }
    
    @Override
    public void cancelar(Scrim scrim) {
        throw new IllegalStateException("No se puede cancelar un scrim en juego");
    }
    
    @Override
    public String getNombreEstado() {
        return "En Juego";
    }
}
