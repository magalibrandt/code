package com.escrims.infrastructure.adapters;

/**
 * PATRÓN ADAPTER
 * Interface que adaptadores deben implementar para diferentes servicios externos.
 */
public interface ExternalServiceAdapter {
    /**
     * Verifica la conectividad con el servicio externo.
     */
    boolean estáDisponible();
    
    /**
     * Obtiene el nombre del servicio.
     */
    String getNombre();
}
