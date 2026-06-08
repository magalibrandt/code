package com.escrims.domain.state;

import com.escrims.domain.model.Scrim;
import com.escrims.domain.model.Usuario;

public interface ScrimState {
    void postular(Scrim scrim, Usuario usuario, String rol);
    void confirmar(Scrim scrim, Usuario usuario);
    void iniciar(Scrim scrim);
    void finalizar(Scrim scrim);
    void cancelar(Scrim scrim);
    String getNombre();

    default String getNombreEstado() {
        return getNombre();
    }
}
