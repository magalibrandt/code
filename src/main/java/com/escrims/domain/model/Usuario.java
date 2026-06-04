package com.escrims.domain.model;

import java.util.*;

public class Usuario {
    private UUID id;
    private String username;
    private String email;
    private String passwordHash;
    private Map<String, String> rangoPorJuego; // juego -> rango (ej: "Valorant" -> "Gold")
    private List<String> rolesPreferidos;
    private String region;
    private PreferenciasUsuario preferencias;
    private int strikes;
    private Date cooldownHasta;
    
    public Usuario(String username, String email, String passwordHash, String region) {
        this.id = UUID.randomUUID();
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.region = region;
        this.rangoPorJuego = new HashMap<>();
        this.rolesPreferidos = new ArrayList<>();
        this.preferencias = new PreferenciasUsuario();
        this.strikes = 0;
    }
    
    // Getters
    public UUID getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public Map<String, String> getRangoPorJuego() { return rangoPorJuego; }
    public List<String> getRolesPreferidos() { return rolesPreferidos; }
    public String getRegion() { return region; }
    public PreferenciasUsuario getPreferencias() { return preferencias; }
    public int getStrikes() { return strikes; }
    public Date getCooldownHasta() { return cooldownHasta; }
    
    // Setters
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setRegion(String region) { this.region = region; }
    public void setPreferencias(PreferenciasUsuario preferencias) { this.preferencias = preferencias; }
    public void setStrikes(int strikes) { this.strikes = strikes; }
    public void setCooldownHasta(Date cooldownHasta) { this.cooldownHasta = cooldownHasta; }
    
    public void agregarRango(String juego, String rango) {
        this.rangoPorJuego.put(juego, rango);
    }
    
    public void agregarRolPreferido(String rol) {
        if (!this.rolesPreferidos.contains(rol)) {
            this.rolesPreferidos.add(rol);
        }
    }
    
    public String getRangoParaJuego(String juego) {
        return rangoPorJuego.getOrDefault(juego, "Unranked");
    }
    
    public boolean estaBajoSancion() {
        return cooldownHasta != null && cooldownHasta.after(new Date());
    }
    
    public void aplicarStrike() {
        this.strikes++;
        if (this.strikes >= 3) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, 7);
            this.cooldownHasta = cal.getTime();
        }
    }
}