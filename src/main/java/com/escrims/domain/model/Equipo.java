package com.escrims.domain.model;

import java.util.*;

public class Equipo {
    private UUID id;
    private String nombre;
    private List<Usuario> jugadores;
    private Map<Usuario, String> rolesAsignados;
    
    public Equipo(String nombre) {
        this.id = UUID.randomUUID();
        this.nombre = nombre;
        this.jugadores = new ArrayList<>();
        this.rolesAsignados = new HashMap<>();
    }
    
    // Getters
    public UUID getId() { return id; }
    public String getLado() { return nombre; }
    public String getNombre() { return nombre; }
    public List<Usuario> getJugadores() { return Collections.unmodifiableList(jugadores); }
    public Map<Usuario, String> getRolesAsignados() { return Collections.unmodifiableMap(rolesAsignados); }
    
    public void agregarJugador(Usuario usuario) {
        agregarJugador(usuario, null);
    }
    
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
            if (rol == null) {
                rolesAsignados.remove(usuario);
            } else {
                rolesAsignados.put(usuario, rol);
            }
        }
    }
    
    public String getRolDeJugador(Usuario usuario) {
        return rolesAsignados.get(usuario);
    }
    
    public boolean contieneJugador(Usuario usuario) {
        return jugadores.contains(usuario);
    }
}
