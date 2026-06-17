package com.escrims.infrastructure.adapters;

/**
 * PATRÓN ADAPTER
 * Adaptador para integración con SendGrid (envío de emails).
 */
public class SendGridAdapter implements ExternalServiceAdapter {
    private String apiKey;
    private String emailFrom;
    private boolean conectado;
    
    public SendGridAdapter(String apiKey) {
        this(apiKey, "noreply@escrims.com");
    }

    public SendGridAdapter(String apiKey, String emailFrom) {
        this.apiKey = apiKey;
        this.emailFrom = emailFrom;
        this.conectado = false;
    }
    
    @Override
    public boolean estaDisponible() {
        return conectado;
    }
    
    public void conectar() {
        System.out.println("[SENDGRID ADAPTER] Validando API Key...");
        // Simular validación
        this.conectado = true;
        System.out.println("[SENDGRID ADAPTER] ✓ Conectado");
    }
    
    public void desconectar() {
        this.conectado = false;
        System.out.println("[SENDGRID ADAPTER] Desconectado");
    }
    
    public boolean enviarEmail(String destinatario, String asunto, String cuerpo) {
        if (!conectado) {
            System.out.println("[SENDGRID] ERROR: No conectado");
            return false;
        }
        
        System.out.println("[SENDGRID] Enviando email:");
        System.out.println("  De: " + emailFrom);
        System.out.println("  Para: " + destinatario);
        System.out.println("  Asunto: " + asunto);
        System.out.println("  Cuerpo: " + cuerpo.substring(0, Math.min(50, cuerpo.length())) + "...");
        return true;
    }
    
    public boolean enviarEmailHtml(String destinatario, String asunto, String htmlCuerpo) {
        if (!conectado) {
            System.out.println("[SENDGRID] ERROR: No conectado");
            return false;
        }
        
        System.out.println("[SENDGRID] Enviando email HTML:");
        System.out.println("  De: " + emailFrom);
        System.out.println("  Para: " + destinatario);
        System.out.println("  Asunto: " + asunto);
        return true;
    }
    
    @Override
    public String getNombre() {
        return "SendGrid";
    }
}
