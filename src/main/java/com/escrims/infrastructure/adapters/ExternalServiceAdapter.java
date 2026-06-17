package com.escrims.infrastructure.adapters;

/**
 * PATRON ADAPTER.
 * Interface que los adaptadores implementan para servicios externos simulados.
 */
public interface ExternalServiceAdapter {
    boolean estaDisponible();

    String getNombre();
}
