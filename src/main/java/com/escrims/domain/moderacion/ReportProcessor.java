package com.escrims.domain.moderacion;

import com.escrims.domain.model.ReporteConducta;

/**
 * PATRÓN CHAIN OF RESPONSIBILITY
 * Interface para procesadores de reportes de conducta.
 */
public interface ReportProcessor {
    /**
     * Procesa un reporte de conducta.
     * Si no puede procesarlo, pasa al siguiente procesador en la cadena.
     */
    void procesarReporte(ReporteConducta reporte);
    
    /**
     * Asigna el siguiente procesador en la cadena.
     */
    void setSiguiente(ReportProcessor siguiente);
    
    /**
     * Obtiene el nivel de este procesador.
     */
    String getNivel();
}
