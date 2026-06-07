package com.escrims.infrastructure.adapters;

/**
 * PATRÓN ADAPTER
 * Adaptador para integración con Discord (webhooks/bots).
 */
public class DiscordAdapter implements ExternalServiceAdapter {
    private String webhookUrl;
    private String botToken;
    private boolean conectado;
    
    public DiscordAdapter(String webhookUrl, String botToken) {
        this.webhookUrl = webhookUrl;
        this.botToken = botToken;
        this.conectado = false;
    }
    
    @Override
    public boolean estaDisponible() {
        // En un caso real, intentaría conectarse a Discord API
        return conectado;
    }
    
    public void conectar() {
        System.out.println("[DISCORD ADAPTER] Conectando a Discord...");
        // Simular conexión
        this.conectado = true;
        System.out.println("[DISCORD ADAPTER] ✓ Conectado");
    }
    
    public void desconectar() {
        this.conectado = false;
        System.out.println("[DISCORD ADAPTER] Desconectado de Discord");
    }
    
    public void enviarMensaje(String canal, String mensaje) {
        if (conectado) {
            System.out.println("[DISCORD] Enviando a #" + canal + ": " + mensaje);
        } else {
            System.out.println("[DISCORD] ERROR: No conectado");
        }
    }
    
    public void enviarNotificacionEmbed(String canal, String titulo, String descripcion) {
        if (conectado) {
            System.out.println("[DISCORD] Embed enviado a #" + canal);
            System.out.println("  Título: " + titulo);
            System.out.println("  Descripción: " + descripcion);
        }
    }
    
    @Override
    public String getNombre() {
        return "Discord";
    }
}
