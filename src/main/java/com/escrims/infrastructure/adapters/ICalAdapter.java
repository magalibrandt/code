package com.escrims.infrastructure.adapters;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * PATRÓN ADAPTER
 * Adaptador para integración con iCal (sincronización de calendarios).
 */
public class ICalAdapter implements ExternalServiceAdapter {
    private String calendarName;
    private String calendarColor;
    private boolean conectado;
    
    public ICalAdapter(String calendarName) {
        this.calendarName = calendarName;
        this.calendarColor = "#4285F4"; // Azul por defecto
        this.conectado = false;
    }
    
    @Override
    public boolean estáDisponible() {
        return conectado;
    }
    
    public void conectar() {
        System.out.println("[ICAL ADAPTER] Iniciando servicio iCal...");
        this.conectado = true;
        System.out.println("[ICAL ADAPTER] ✓ Conectado");
    }
    
    public void desconectar() {
        this.conectado = false;
        System.out.println("[ICAL ADAPTER] Desconectado");
    }
    
    public String generarEventoICS(String titulo, LocalDateTime inicio, int duracionMinutos, String descripcion) {
        if (!conectado) {
            System.out.println("[ICAL] ERROR: No conectado");
            return null;
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");
        LocalDateTime fin = inicio.plusMinutes(duracionMinutos);
        
        StringBuilder ics = new StringBuilder();
        ics.append("BEGIN:VCALENDAR\n");
        ics.append("VERSION:2.0\n");
        ics.append("PRODID:-//eScrims//eScrims//EN\n");
        ics.append("BEGIN:VEVENT\n");
        ics.append("DTSTART:").append(inicio.format(formatter)).append("\n");
        ics.append("DTEND:").append(fin.format(formatter)).append("\n");
        ics.append("SUMMARY:").append(titulo).append("\n");
        ics.append("DESCRIPTION:").append(descripcion).append("\n");
        ics.append("END:VEVENT\n");
        ics.append("END:VCALENDAR\n");
        
        System.out.println("[ICAL] Evento generado: " + titulo);
        return ics.toString();
    }
    
    public void exportarEvento(String titulo, LocalDateTime inicio, int duracionMinutos) {
        String ics = generarEventoICS(titulo, inicio, duracionMinutos, "Scrim eSports");
        System.out.println("[ICAL] Evento listo para exportar a calendario");
    }
    
    @Override
    public String getNombre() {
        return "iCal";
    }
}
