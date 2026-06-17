package com.escrims.domain.moderacion;

import com.escrims.domain.model.ReporteConducta;
import com.escrims.domain.model.Sancion;

/**
 * PATRÓN CHAIN OF RESPONSIBILITY - Handler Concreto 3
 * Procesador Humano: revisión y resolución final por moderador.
 */
public class HumanModeratorProcessor implements ReportProcessor {
    private ReportProcessor siguiente;
    
    @Override
    public void procesarReporte(ReporteConducta reporte) {
        System.out.println("[HUMAN-MODERATOR] Revisión manual del reporte por moderador...");
        System.out.println("  Reportador: " + reporte.getReportador().getUsername());
        System.out.println("  Reportado: " + reporte.getReportado().getUsername());
        System.out.println("  Motivo: " + reporte.getMotivo());
        System.out.println("  Descripción: " + reporte.getDescripcion());
        
        // El moderador siempre resuelve (fin de la cadena)
        // En un sistema real, esto sería manual o con más lógica compleja
        
        if (reporte.getReportado().getStrikes() >= 5) {
            System.out.println("  → Decisión: BAN PERMANENTE (demasiados strikes previos)");
            reporte.marcarResuelto(Sancion.banPermanente(), "Baneado por acumulacion de sanciones", "Moderator");
        } else if (reporte.getMotivo().toLowerCase().contains("fraude") || 
                   reporte.getMotivo().toLowerCase().contains("smurfing")) {
            System.out.println("  → Decisión: SUSPENSIÓN 7 DÍAS (fraude/smurfing)");
            reporte.marcarResuelto(Sancion.suspension7d(), "Suspension por fraude o smurfing confirmado", "Moderator");
        } else {
            System.out.println("  → Decisión: ADVERTENCIA (incidente leve)");
            reporte.marcarResuelto(Sancion.advertencia(), "Advertencia formal de moderador", "Moderator");
        }
        
        // No hay siguiente en la cadena - fin del procesamiento
    }
    
    @Override
    public void setSiguiente(ReportProcessor siguiente) {
        // El moderador humano es el final, ignorar siguiente
        this.siguiente = siguiente;
    }
    
    @Override
    public String getNivel() {
        return "HUMAN MODERATOR";
    }
}
