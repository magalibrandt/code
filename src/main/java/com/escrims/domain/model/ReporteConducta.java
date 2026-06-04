package com.escrims.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad que representa un reporte de conducta en un Scrim.
 * Utilizado por el patrón Chain of Responsibility en moderación.
 */
public class ReporteConducta {
    public enum EstadoReporte {
        PENDIENTE, EN_REVISION, RESUELTO, RECHAZADO
    }
    
    public enum Sancion {
        NINGUNA, ADVERTENCIA, SUSPENSION_24H, SUSPENSION_7D, BAN_PERMANENTE
    }
    
    private UUID id;
    private Scrim scrim;
    private Usuario reportador;
    private Usuario reportado;
    private String motivo;
    private String descripcion;
    private EstadoReporte estado;
    private Sancion sancion;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaResolucion;
    private String notas;
    private String procesadorResolvio; // Quién resolvió (bot, mod, admin)
    
    public ReporteConducta(Scrim scrim, Usuario reportador, Usuario reportado, String motivo, String descripcion) {
        this.id = UUID.randomUUID();
        this.scrim = scrim;
        this.reportador = reportador;
        this.reportado = reportado;
        this.motivo = motivo;
        this.descripcion = descripcion;
        this.estado = EstadoReporte.PENDIENTE;
        this.sancion = Sancion.NINGUNA;
        this.fechaCreacion = LocalDateTime.now();
    }
    
    // Getters
    public UUID getId() { return id; }
    public Scrim getScrim() { return scrim; }
    public Usuario getReportador() { return reportador; }
    public Usuario getReportado() { return reportado; }
    public String getMotivo() { return motivo; }
    public String getDescripcion() { return descripcion; }
    public EstadoReporte getEstado() { return estado; }
    public Sancion getSancion() { return sancion; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public LocalDateTime getFechaResolucion() { return fechaResolucion; }
    public String getNotas() { return notas; }
    public String getProcesadorResolvio() { return procesadorResolvio; }
    
    // Setters
    public void setEstado(EstadoReporte estado) { this.estado = estado; }
    public void setSancion(Sancion sancion) { this.sancion = sancion; }
    public void setFechaResolucion(LocalDateTime fecha) { this.fechaResolucion = fecha; }
    public void setNotas(String notas) { this.notas = notas; }
    public void setProcesadorResolvio(String procesador) { this.procesadorResolvio = procesador; }
    
    public boolean esReportePendiente() {
        return estado == EstadoReporte.PENDIENTE;
    }
    
    public void marcarResuelto(Sancion sancion, String notas, String procesador) {
        this.sancion = sancion;
        this.notas = notas;
        this.procesadorResolvio = procesador;
        this.estado = EstadoReporte.RESUELTO;
        this.fechaResolucion = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return String.format("Reporte [id=%s, motivo=%s, estado=%s, sancion=%s, procesador=%s]",
            id, motivo, estado, sancion, procesadorResolvio);
    }
}
