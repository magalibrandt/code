package com.escrims.domain.model;

import java.util.*;

public class Estadistica {
    private UUID id;
    private Scrim scrim;
    private Map<Usuario, EstadisticaJugador> estadisticasPorJugador;
    private String equipoGanador; // "A", "B" o "Empate"
    private String observaciones;
    
    public Estadistica(Scrim scrim) {
        this.id = UUID.randomUUID();
        this.scrim = scrim;
        this.estadisticasPorJugador = new HashMap<>();
    }
    
    // Getters
    public UUID getId() { return id; }
    public Scrim getScrim() { return scrim; }
    public Map<Usuario, EstadisticaJugador> getEstadisticasPorJugador() { return estadisticasPorJugador; }
    public String getEquipoGanador() { return equipoGanador; }
    public String getObservaciones() { return observaciones; }
    
    // Setters
    public void setEquipoGanador(String equipoGanador) { this.equipoGanador = equipoGanador; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    
    public void agregarEstadisticaJugador(Usuario usuario, EstadisticaJugador stats) {
        this.estadisticasPorJugador.put(usuario, stats);
    }
}
