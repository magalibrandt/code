package com.escrims.domain.strategy;

import com.escrims.domain.model.Usuario;
import com.escrims.domain.model.Scrim;
import java.util.*;
import java.util.stream.Collectors;

/**
 * PATRÓN STRATEGY - Estrategia Concreta
 * Selecciona jugadores basándose en su latencia/ping.
 */
public class ByLatencyStrategy implements MatchmakingStrategy {
    
    // Simulación de latencias por región
    private Map<String, Map<String, Integer>> latenciasPorRegion;
    
    public ByLatencyStrategy() {
        this.latenciasPorRegion = new HashMap<>();
        inicializarLatencias();
    }
    
    private void inicializarLatencias() {
        // Simulación: latencias entre regiones
        Map<String, Integer> latenciasNA = new HashMap<>();
        latenciasNA.put("NA", 20);
        latenciasNA.put("EU", 120);
        latenciasNA.put("LATAM", 60);
        latenciasNA.put("BR", 80);
        latenciasPorRegion.put("NA", latenciasNA);
        
        Map<String, Integer> latenciasEU = new HashMap<>();
        latenciasEU.put("NA", 120);
        latenciasEU.put("EU", 20);
        latenciasEU.put("LATAM", 150);
        latenciasEU.put("BR", 140);
        latenciasPorRegion.put("EU", latenciasEU);
        
        Map<String, Integer> latenciasLATAM = new HashMap<>();
        latenciasLATAM.put("NA", 60);
        latenciasLATAM.put("EU", 150);
        latenciasLATAM.put("LATAM", 20);
        latenciasLATAM.put("BR", 40);
        latenciasPorRegion.put("LATAM", latenciasLATAM);
        
        Map<String, Integer> latenciasBR = new HashMap<>();
        latenciasBR.put("NA", 80);
        latenciasBR.put("EU", 140);
        latenciasBR.put("LATAM", 40);
        latenciasBR.put("BR", 20);
        latenciasPorRegion.put("BR", latenciasBR);
    }
    
    @Override
    public List<Usuario> seleccionar(List<Usuario> candidatos, Scrim scrim) {
        return candidatos.stream()
            .filter(u -> {
                int latencia = calcularLatencia(u.getRegion(), scrim.getRegion());
                return latencia <= scrim.getLatenciaMax();
            })
            .sorted((u1, u2) -> {
                int lat1 = calcularLatencia(u1.getRegion(), scrim.getRegion());
                int lat2 = calcularLatencia(u2.getRegion(), scrim.getRegion());
                return Integer.compare(lat1, lat2);
            })
            .limit(scrim.getCuposTotales())
            .collect(Collectors.toList());
    }
    
    private int calcularLatencia(String regionUsuario, String regionScrim) {
        return latenciasPorRegion
            .getOrDefault(regionUsuario, new HashMap<>())
            .getOrDefault(regionScrim, 999);
    }
    
    @Override
    public String getNombreEstrategia() {
        return "Emparejamiento por Latencia";
    }
}
