package com.escrims.domain.strategy;

import com.escrims.domain.model.Usuario;
import com.escrims.domain.model.Scrim;
import java.util.*;
import java.util.stream.Collectors;

/**
 * PATRÓN STRATEGY - Estrategia Concreta
 * Selecciona jugadores basándose en su rango/MMR.
 */
public class ByMMRStrategy implements MatchmakingStrategy {
    
    private static final Map<String, Integer> RANGO_VALORES = new HashMap<>();
    
    static {
        RANGO_VALORES.put("Iron", 1);
        RANGO_VALORES.put("Bronze", 2);
        RANGO_VALORES.put("Silver", 3);
        RANGO_VALORES.put("Gold", 4);
        RANGO_VALORES.put("Platinum", 5);
        RANGO_VALORES.put("Diamond", 6);
        RANGO_VALORES.put("Immortal", 7);
        RANGO_VALORES.put("Radiant", 8);
    }
    
    @Override
    public List<Usuario> seleccionar(List<Usuario> candidatos, Scrim scrim) {
        int rangoMinValor = RANGO_VALORES.getOrDefault(scrim.getRangoMin(), 1);
        int rangoMaxValor = RANGO_VALORES.getOrDefault(scrim.getRangoMax(), 8);
        
        return candidatos.stream()
            .filter(u -> {
                String rangoUsuario = u.getRangoParaJuego(scrim.getJuego());
                int valorRango = RANGO_VALORES.getOrDefault(rangoUsuario, 0);
                return valorRango >= rangoMinValor && valorRango <= rangoMaxValor;
            })
            .limit(scrim.getCuposTotales())
            .collect(Collectors.toList());
    }
    
    @Override
    public String getNombreEstrategia() {
        return "Emparejamiento por MMR/Rango";
    }
}
