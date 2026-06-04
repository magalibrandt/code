package com.escrims.domain.model;

import java.util.*;

public class PreferenciasUsuario {
    private List<String> juegosPreferidos;
    private int latenciaMaxima;
    private List<String> horariosDisponibles;
    private boolean recibirNotificacionesPush;
    private boolean recibirNotificacionesEmail;
    private boolean recibirNotificacionesDiscord;
    
    public PreferenciasUsuario() {
        this.juegosPreferidos = new ArrayList<>();
        this.latenciaMaxima = 100;
        this.horariosDisponibles = new ArrayList<>();
        this.recibirNotificacionesPush = true;
        this.recibirNotificacionesEmail = true;
        this.recibirNotificacionesDiscord = false;
    }
    
    // Getters
    public List<String> getJuegosPreferidos() { return juegosPreferidos; }
    public int getLatenciaMaxima() { return latenciaMaxima; }
    public List<String> getHorariosDisponibles() { return horariosDisponibles; }
    public boolean isRecibirNotificacionesPush() { return recibirNotificacionesPush; }
    public boolean isRecibirNotificacionesEmail() { return recibirNotificacionesEmail; }
    public boolean isRecibirNotificacionesDiscord() { return recibirNotificacionesDiscord; }
    
    // Setters
    public void setLatenciaMaxima(int latenciaMaxima) { this.latenciaMaxima = latenciaMaxima; }
    public void setRecibirNotificacionesPush(boolean recibirNotificacionesPush) { 
        this.recibirNotificacionesPush = recibirNotificacionesPush; 
    }
    public void setRecibirNotificacionesEmail(boolean recibirNotificacionesEmail) { 
        this.recibirNotificacionesEmail = recibirNotificacionesEmail; 
    }
    public void setRecibirNotificacionesDiscord(boolean recibirNotificacionesDiscord) { 
        this.recibirNotificacionesDiscord = recibirNotificacionesDiscord; 
    }
}
