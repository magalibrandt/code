package com.escrims.domain.moderacion;

import com.escrims.domain.model.ReporteConducta;
import com.escrims.domain.model.ReporteConducta.Sancion;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * PATRÓN CHAIN OF RESPONSIBILITY - Handler Concreto 1
 * Procesador Automático: resuelve casos claros automáticamente.
 */
public class AutomaticProcessor implements ReportProcessor {
    private ReportProcessor siguiente;
    
    @Override
    public void procesarReporte(ReporteConducta reporte) {
        System.out.println("[AUTO-PROCESSOR] Analizando reporte: " + reporte.getMotivo());
        
        // Casos que se pueden auto-resolver
        if (reporte.getMotivo().equalsIgnoreCase("SPAM")) {
            System.out.println("  → Caso de SPAM detectado. Aplicando sanción automática.");
            reporte.marcarResuelto(Sancion.ADVERTENCIA, "Spam automáticamente detectado y sancionado", "AutoBot");
            return;
        }
        
        if (reporte.getMotivo().equalsIgnoreCase("INACTIVIDAD") && reporte.getDescripcion().toLowerCase().contains("no show")) {
            System.out.println("  → No-show detectado. Aplicando strike.");
            reporte.getReportado().aplicarStrike();
            reporte.marcarResuelto(Sancion.ADVERTENCIA, "No-show registrado, strike aplicado", "AutoBot");
            return;
        }
        
        // Si no puede resolver, pasar al siguiente
        if (siguiente != null) {
            System.out.println("  → No se puede auto-resolver. Escalando a " + siguiente.getNivel());
            siguiente.procesarReporte(reporte);
        }
    }
    
    @Override
    public void setSiguiente(ReportProcessor siguiente) {
        this.siguiente = siguiente;
    }
    
    @Override
    public String getNivel() {
        return "AUTOMATIC PROCESSOR";
    }
}
