package com.escrims.domain.model;

import com.escrims.domain.state.BuscandoJugadoresState;
import com.escrims.domain.state.ScrimState;
import java.time.LocalDateTime;
import java.util.*;

public class Scrim {
    private UUID id;
    private String juego;
    private String formato; // "5v5", "3v3", "1v1"
    private String region;
    private String rangoMin;
    private String rangoMax;
    private int latenciaMax;
    private LocalDateTime fechaHora;
    private int duracionEstimada; // en minutos
    private String modalidad; // "ranked-like", "casual", "practica"
    private int cuposTotales;
    private Usuario creador;
    private ScrimState estado;
    private List<Postulacion> postulaciones;
    private List<Confirmacion> confirmaciones;
    private Map<String, Integer> rolesPorLado; // rol -> cantidad requerida
    private Equipo equipoA;
    private Equipo equipoB;
    private Estadistica estadistica;
    
    public Scrim(String juego, String formato, String region, Usuario creador) {
        this.id = UUID.randomUUID();
        this.juego = juego;
        this.formato = formato;
        this.region = region;
        this.creador = creador;
        this.estado = new BuscandoJugadoresState();
        this.postulaciones = new ArrayList<>();
        this.confirmaciones = new ArrayList<>();
        this.rolesPorLado = new HashMap<>();
        this.equipoA = new Equipo("A");
        this.equipoB = new Equipo("B");
    }
    
    // Getters
    public UUID getId() { return id; }
    public String getJuego() { return juego; }
    public String getFormato() { return formato; }
    public String getRegion() { return region; }
    public String getRangoMin() { return rangoMin; }
    public String getRangoMax() { return rangoMax; }
    public int getLatenciaMax() { return latenciaMax; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public int getDuracionEstimada() { return duracionEstimada; }
    public String getModalidad() { return modalidad; }
    public int getCuposTotales() { return cuposTotales; }
    public Usuario getCreador() { return creador; }
    public ScrimState getEstado() { return estado; }
    public List<Postulacion> getPostulaciones() { return postulaciones; }
    public List<Confirmacion> getConfirmaciones() { return confirmaciones; }
    public Map<String, Integer> getRolesPorLado() { return rolesPorLado; }
    public Equipo getEquipoA() { return equipoA; }
    public Equipo getEquipoB() { return equipoB; }
    public Estadistica getEstadistica() { return estadistica; }
    
    // Setters
    public void setRangoMin(String rangoMin) { this.rangoMin = rangoMin; }
    public void setRangoMax(String rangoMax) { this.rangoMax = rangoMax; }
    public void setLatenciaMax(int latenciaMax) { this.latenciaMax = latenciaMax; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    public void setDuracionEstimada(int duracionEstimada) { this.duracionEstimada = duracionEstimada; }
    public void setModalidad(String modalidad) { this.modalidad = modalidad; }
    public void setCuposTotales(int cuposTotales) { this.cuposTotales = cuposTotales; }
    public void setEstado(ScrimState estado) { this.estado = estado; }
    public void setEstadistica(Estadistica estadistica) { this.estadistica = estadistica; }
    
    // Métodos delegados al State
    public void postular(Usuario usuario, String rolDeseado) {
        estado.postular(this, usuario, rolDeseado);
    }
    
    public void confirmar(Usuario usuario) {
        estado.confirmar(this, usuario);
    }
    
    public void iniciar() {
        estado.iniciar(this);
    }
    
    public void finalizar() {
        estado.finalizar(this);
    }
    
    public void cancelar() {
        estado.cancelar(this);
    }
    
    public void agregarPostulacion(Postulacion postulacion) {
        this.postulaciones.add(postulacion);
    }
    
    public void agregarConfirmacion(Confirmacion confirmacion) {
        this.confirmaciones.add(confirmacion);
    }
    
    public boolean estaCompleto() {
        long aceptadas = postulaciones.stream()
            .filter(p -> p.getEstado() == EstadoPostulacion.ACEPTADA)
            .count();
        return aceptadas >= cuposTotales;
    }
    
    public boolean todosConfirmaron() {
        return confirmaciones.size() >= cuposTotales && 
               confirmaciones.stream().allMatch(Confirmacion::isConfirmado);
    }
    
    /**
     * Ejecuta el matchmaking y puebla los equipos A y B
     * Se llamará automáticamente cuando el cupo se complete
     */
    public void ejecutarMatchmaking(List<Usuario> usuariosSeleccionados) {
        // Distribuir mitad a cada equipo
        int mitad = usuariosSeleccionados.size() / 2;
        
        for (int i = 0; i < usuariosSeleccionados.size(); i++) {
            Usuario usuario = usuariosSeleccionados.get(i);
            // Obtener el rol deseado de la postulación del usuario
            String rol = postulaciones.stream()
                .filter(p -> p.getUsuario().equals(usuario) && 
                           p.getEstado() == EstadoPostulacion.ACEPTADA)
                .findFirst()
                .map(Postulacion::getRolDeseado)
                .orElse("Soporte"); // Default si no se encuentra
            
            if (i < mitad) {
                equipoA.agregarJugador(usuario, rol);
            } else {
                equipoB.agregarJugador(usuario, rol);
            }
        }
    }
    
    public String getNombreEstado() {
        return estado.getNombreEstado();
    }
}
