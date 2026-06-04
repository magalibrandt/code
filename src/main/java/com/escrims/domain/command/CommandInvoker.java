package com.escrims.domain.command;

import com.escrims.domain.model.Scrim;
import java.util.Stack;
import java.util.ArrayList;
import java.util.List;

/**
 * PATRÓN COMMAND - Invoker
 * Gestiona la ejecución, deshacer y rehacer de comandos.
 */
public class CommandInvoker {
    private Stack<ScrimCommand> comandosEjecutados;
    private Stack<ScrimCommand> comandosDeshechados;
    private Scrim scrim;
    
    public CommandInvoker(Scrim scrim) {
        this.scrim = scrim;
        this.comandosEjecutados = new Stack<>();
        this.comandosDeshechados = new Stack<>();
    }
    
    /**
     * Ejecuta un comando y lo almacena en el historial.
     */
    public void ejecutar(ScrimCommand comando) {
        comando.execute(scrim);
        comandosEjecutados.push(comando);
        // Limpiar historial de rehacer al ejecutar nuevo comando
        comandosDeshechados.clear();
        System.out.println("[COMMAND] Ejecutado: " + comando.getDescription());
    }
    
    /**
     * Deshace el último comando ejecutado.
     */
    public void deshacer() {
        if (!comandosEjecutados.isEmpty()) {
            ScrimCommand comando = comandosEjecutados.pop();
            comando.undo(scrim);
            comandosDeshechados.push(comando);
            System.out.println("[COMMAND] Deshecho: " + comando.getDescription());
        }
    }
    
    /**
     * Rehace el último comando deshecho.
     */
    public void rehacer() {
        if (!comandosDeshechados.isEmpty()) {
            ScrimCommand comando = comandosDeshechados.pop();
            comando.execute(scrim);
            comandosEjecutados.push(comando);
            System.out.println("[COMMAND] Rehecho: " + comando.getDescription());
        }
    }
    
    /**
     * Obtiene el historial de comandos ejecutados.
     */
    public List<String> obtenerHistorial() {
        List<String> historial = new ArrayList<>();
        for (ScrimCommand cmd : comandosEjecutados) {
            historial.add(cmd.getDescription());
        }
        return historial;
    }
    
    /**
     * Limpia el historial de comandos.
     */
    public void limpiarHistorial() {
        comandosEjecutados.clear();
        comandosDeshechados.clear();
    }
}
