package com.escrims.domain.moderacion;

import com.escrims.domain.model.ReporteConducta;
import com.escrims.domain.model.Sancion;

/**
 * PATRÓN CHAIN OF RESPONSIBILITY - Handler Concreto 2
 * Procesador Bot: analiza patrones y aplica sanciones algorítmicamente.
 */
public class BotProcessor implements ReportProcessor {
    private ReportProcessor siguiente;
    
    @Override
    public void procesarReporte(ReporteConducta reporte) {
        System.out.println("[BOT-PROCESSOR] Analizando reportes previos y patrones...");
        
        // Analizar si el usuario reportado tiene historial de problemas
        int strikesActuales = reporte.getReportado().getStrikes();
        
        if (strikesActuales >= 3) {
            System.out.println("  → Usuario con múltiples strikes (" + strikesActuales + "). Aplicando suspensión.");
            reporte.marcarResuelto(Sancion.suspension24h(), "Suspension automatica por multiples strikes", "ModBot");
            return;
        }
        
        if (strikesActuales >= 1 && reporte.getMotivo().toLowerCase().contains("conducta")) {
            System.out.println("  → Conducta inapropiada reincidente. Escalando a moderador humano.");
            if (siguiente != null) {
                siguiente.procesarReporte(reporte);
            }
            return;
        }
        
        // Casos que el bot puede resolver
        if (reporte.getMotivo().equalsIgnoreCase("LENGUAJE_OFENSIVO")) {
            System.out.println("  → Lenguaje ofensivo detectado. Aplicando sanción.");
            reporte.marcarResuelto(Sancion.advertencia(), "Contenido ofensivo moderado por bot", "ModBot");
            return;
        }
        
        // Si no puede resolver, escalara a moderador humano
        if (siguiente != null) {
            System.out.println("  → Caso requiere revisión humana. Escalando a " + siguiente.getNivel());
            siguiente.procesarReporte(reporte);
        }
    }
    
    @Override
    public void setSiguiente(ReportProcessor siguiente) {
        this.siguiente = siguiente;
    }
    
    @Override
    public String getNivel() {
        return "BOT PROCESSOR";
    }
}
