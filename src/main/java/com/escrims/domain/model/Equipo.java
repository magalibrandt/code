package com.escrims.domain.model;

import java.util.*;

public class Equipo {
    private UUID id;
    private String lado; // "A" o "B"
    private List<Usuario> jugadores;
    private Map<Usuario, String> rolesAsignados;
    
    public Equipo(String lado) {
        this.id = UUID.randomUUID();
        this.lado = lado;
        this.jugadores = new ArrayList<>();
        this.rolesAsignados = new HashMap<>();
    }
    
    // Getters
    public UUID getId() { return id; }
    public String getLado() { return lado; }
    public List<Usuario> getJugadores() { return jugadores; }
    public Map<Usuario, String> getRolesAsignados() { return rolesAsignados; }
    
    public void agregarJugador(Usuario usuario, String rol) {
        if (!jugadores.contains(usuario)) {
            jugadores.add(usuario);
            rolesAsignados.put(usuario, rol);
        }
    }
    
    public void removerJugador(Usuario usuario) {
        jugadores.remove(usuario);
        rolesAsignados.remove(usuario);
    }
    
    public void asignarRol(Usuario usuario, String rol) {
        if (jugadores.contains(usuario)) {
            rolesAsignados.put(usuario, rol);
        }
    }
    
    public String getRolDeJugador(Usuario usuario) {
        return rolesAsignados.get(usuario);
    }
}
