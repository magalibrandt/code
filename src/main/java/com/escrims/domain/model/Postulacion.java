package com.escrims.domain.model;

import java.util.*;

public class Postulacion {
    private UUID id;
    private Usuario usuario;
    private Scrim scrim;
    private String rolDeseado;
    private EstadoPostulacion estado;
    private Date fechaPostulacion;
    
    public Postulacion(Usuario usuario, Scrim scrim, String rolDeseado) {
        this.id = UUID.randomUUID();
        this.usuario = usuario;
        this.scrim = scrim;
        this.rolDeseado = rolDeseado;
        this.estado = EstadoPostulacion.pendiente();
        this.fechaPostulacion = new Date();
    }
    
    // Constructor de copia (para el patrón Command)
    public Postulacion(Postulacion otra) {
        this.id = otra.id;
        this.usuario = otra.usuario;
        this.scrim = otra.scrim;
        this.rolDeseado = otra.rolDeseado;
        this.estado = otra.estado;
        this.fechaPostulacion = new Date(otra.fechaPostulacion.getTime());
    }
    
    // Getters
    public UUID getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public Scrim getScrim() { return scrim; }
    public String getRolDeseado() { return rolDeseado; }
    public EstadoPostulacion getEstado() { return estado; }
    public Date getFechaPostulacion() { return fechaPostulacion; }
    
    // Setters
    public void setEstado(EstadoPostulacion estado) { this.estado = estado; }
    public void setRolDeseado(String rolDeseado) { this.rolDeseado = rolDeseado; }
    
    public void aceptar() {
        this.estado = EstadoPostulacion.aceptada();
    }
    
    public void rechazar() {
        this.estado = EstadoPostulacion.rechazada();
    }
}
