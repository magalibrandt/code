package com.escrims.domain.state;

import com.escrims.domain.model.*;

/**
 * PATRÓN STATE - Estado Concreto
 * Representa el estado donde el scrim fue cancelado.
 */
public class CanceladoState implements ScrimState {
    
    @Override
    public void postular(Scrim scrim, Usuario usuario, String rolDeseado) {
        throw new IllegalStateException("El scrim está cancelado");
    }
    
    @Override
    public void confirmar(Scrim scrim, Usuario usuario) {
        throw new IllegalStateException("El scrim está cancelado");
    }
    
    @Override
    public void iniciar(Scrim scrim) {
        throw new IllegalStateException("El scrim está cancelado");
    }
    
    @Override
    public void finalizar(Scrim scrim) {
        throw new IllegalStateException("El scrim está cancelado");
    }
    
    @Override
    public void cancelar(Scrim scrim) {
        throw new IllegalStateException("El scrim ya está cancelado");
    }
    
    @Override
    public String getNombre() {
        return "Cancelado";
    }
}
