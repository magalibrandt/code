package com.escrims.domain.model;

import java.util.*;

public class Confirmacion {
    private UUID id;
    private Usuario usuario;
    private Scrim scrim;
    private boolean confirmado;
    private Date fechaConfirmacion;
    
    public Confirmacion(Usuario usuario, Scrim scrim) {
        this.id = UUID.randomUUID();
        this.usuario = usuario;
        this.scrim = scrim;
        this.confirmado = false;
    }
    
    // Getters
    public UUID getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public Scrim getScrim() { return scrim; }
    public boolean isConfirmado() { return confirmado; }
    public Date getFechaConfirmacion() { return fechaConfirmacion; }
    
    public void confirmar() {
        this.confirmado = true;
        this.fechaConfirmacion = new Date();
    }
}
