package com.escrims.domain.state;

import com.escrims.domain.model.*;

/**
 * PATRÓN STATE - Estado Concreto
 * Representa el estado final donde el scrim ha terminado.
 */
public class FinalizadoState implements ScrimState {
    
    @Override
    public void postular(Scrim scrim, Usuario usuario, String rolDeseado) {
        throw new IllegalStateException("El scrim ya finalizó");
    }
    
    @Override
    public void confirmar(Scrim scrim, Usuario usuario) {
        throw new IllegalStateException("El scrim ya finalizó");
    }
    
    @Override
    public void iniciar(Scrim scrim) {
        throw new IllegalStateException("El scrim ya finalizó");
    }
    
    @Override
    public void finalizar(Scrim scrim) {
        throw new IllegalStateException("El scrim ya está finalizado");
    }
    
    @Override
    public void cancelar(Scrim scrim) {
        throw new IllegalStateException("No se puede cancelar un scrim finalizado");
    }
    
    @Override
    public String getNombreEstado() {
        return "Finalizado";
    }
}
