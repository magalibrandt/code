package com.escrims.domain.model;

public class EstadisticaJugador {
    private int kills;
    private int deaths;
    private int assists;
    private boolean mvp;
    private int rating;
    
    public EstadisticaJugador() {
        this.kills = 0;
        this.deaths = 0;
        this.assists = 0;
        this.mvp = false;
        this.rating = 0;
    }
    
    // Getters
    public int getKills() { return kills; }
    public int getDeaths() { return deaths; }
    public int getAssists() { return assists; }
    public boolean isMvp() { return mvp; }
    public int getRating() { return rating; }
    
    // Setters
    public void setKills(int kills) { this.kills = kills; }
    public void setDeaths(int deaths) { this.deaths = deaths; }
    public void setAssists(int assists) { this.assists = assists; }
    public void setMvp(boolean mvp) { this.mvp = mvp; }
    public void setRating(int rating) { this.rating = rating; }
    
    public double getKDA() {
        if (deaths == 0) return kills + assists;
        return (double)(kills + assists) / deaths;
    }
}
