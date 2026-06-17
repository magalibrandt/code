package com.escrims.domain.model;

import java.util.Date;
import java.util.UUID;

/**
 * Entidad que representa un reporte de conducta en un Scrim.
 * Utilizado por el patron Chain of Responsibility en moderacion.
 */
public class ReporteConducta {
    private UUID id;
    private Scrim scrim;
    private Usuario reportador;
    private Usuario reportado;
    private String motivo;
    private String descripcion;
    private EstadoReporte estado;
    private Sancion sancion;
    private Date fechaCreacion;
    private Date fechaResolucion;
    private String notas;
    private String procesadorResolvio;

    public ReporteConducta(Scrim scrim, Usuario reportador, Usuario reportado, String motivo, String descripcion) {
        this.id = UUID.randomUUID();
        this.scrim = scrim;
        this.reportador = reportador;
        this.reportado = reportado;
        this.motivo = motivo;
        this.descripcion = descripcion;
        this.estado = EstadoReporte.pendiente();
        this.sancion = Sancion.ninguna();
        this.fechaCreacion = new Date();
    }

    public UUID getId() { return id; }
    public Scrim getScrim() { return scrim; }
    public Usuario getReportador() { return reportador; }
    public Usuario getReportado() { return reportado; }
    public String getMotivo() { return motivo; }
    public String getDescripcion() { return descripcion; }
    public EstadoReporte getEstado() { return estado; }
    public Sancion getSancion() { return sancion; }
    public Date getFechaCreacion() { return fechaCreacion; }
    public Date getFechaResolucion() { return fechaResolucion; }
    public String getNotas() { return notas; }
    public String getProcesadorResolvio() { return procesadorResolvio; }

    public void setEstado(EstadoReporte estado) {
        if (estado == null) {
            throw new IllegalArgumentException("El estado del reporte es obligatorio");
        }
        this.estado = estado;
    }

    public void setSancion(Sancion sancion) {
        if (sancion == null) {
            throw new IllegalArgumentException("La sancion es obligatoria");
        }
        this.sancion = sancion;
    }

    public boolean esReportePendiente() {
        return estado.esPendiente();
    }

    public void marcarResuelto(Sancion sancion, String notas, String procesador) {
        setSancion(sancion);
        this.notas = notas;
        this.procesadorResolvio = procesador;
        this.estado = EstadoReporte.resuelto();
        this.fechaResolucion = new Date();
    }

    @Override
    public String toString() {
        return String.format("Reporte [id=%s, motivo=%s, estado=%s, sancion=%s, procesador=%s]",
            id, motivo, estado, sancion, procesadorResolvio);
    }
}
