package com.escrims.domain.strategy;

import com.escrims.domain.model.Usuario;
import com.escrims.domain.model.Scrim;
import java.util.List;

/**
 * PATRÓN STRATEGY
 * Interface que define el contrato para algoritmos de emparejamiento.
 * Permite intercambiar algoritmos en tiempo de ejecución.
 */
public interface MatchmakingStrategy {
    List<Usuario> seleccionar(List<Usuario> candidatos, Scrim scrim);
    String getNombreEstrategia();
}
