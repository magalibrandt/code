package com.escrims.domain.strategy;

import com.escrims.domain.model.Usuario;
import com.escrims.domain.model.Scrim;
import java.util.*;
import java.util.stream.Collectors;

/**
 * PATRÓN STRATEGY - Estrategia Concreta
 * Selecciona jugadores basándose en su historial y comportamiento.
 */
public class ByHistoryStrategy implements MatchmakingStrategy {
    
    @Override
    public List<Usuario> seleccionar(List<Usuario> candidatos, Scrim scrim) {
        return candidatos.stream()
            .filter(u -> !u.estaBajoSancion()) // Excluir sancionados
            .filter(u -> u.getStrikes() < 2) // Preferir usuarios con buen comportamiento
            .sorted((u1, u2) -> Integer.compare(u1.getStrikes(), u2.getStrikes()))
            .limit(scrim.getCuposTotales())
            .collect(Collectors.toList());
    }
    
    @Override
    public String getNombreEstrategia() {
        return "Emparejamiento por Historial";
    }
}
