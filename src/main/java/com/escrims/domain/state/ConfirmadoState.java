package com.escrims.domain.state;

import com.escrims.domain.model.*;
import com.escrims.domain.events.*;

/**
 * PATRÓN STATE - Estado Concreto
 * Representa el estado donde todos confirmaron y se espera la hora de inicio.
 */
public class ConfirmadoState implements ScrimState {
    
    @Override
    public void postular(Scrim scrim, Usuario usuario, String rolDeseado) {
        throw new IllegalStateException("El scrim ya está confirmado");
    }
    
    @Override
    public void confirmar(Scrim scrim, Usuario usuario) {
        throw new IllegalStateException("Ya todos confirmaron");
    }
    
    @Override
    public void iniciar(Scrim scrim) {
        scrim.setEstado(new EnJuegoState());
        DomainEventBus.getInstance().publish(
            new ScrimStateChangedEvent(scrim.getId(), "EnJuego")
        );
    }
    
    @Override
    public void finalizar(Scrim scrim) {
        throw new IllegalStateException("Debe iniciar antes de finalizar");
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
        return "Confirmado";
    }
}
