package com.escrims.application;

import com.escrims.application.builder.ScrimBuilder;
import com.escrims.domain.command.*;
import com.escrims.domain.events.*;
import com.escrims.domain.model.*;
import com.escrims.domain.strategy.*;
import com.escrims.infrastructure.adapters.*;
import com.escrims.infrastructure.notifications.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * PATRÓN FACADE
 * 
 * Interface simplificada que proporciona una fachada unificada al sistema eScrims.
 * 
 * El Facade encapsula la complejidad de múltiples subsistemas:
 * - ScrimService (gestión de scrims)
 * - DomainEventBus (eventos)
 * - CommandInvoker (comandos)
 * - ReportProcessor (moderación)
 * - Notificadores (multi-canal)
 * - Adaptadores (integraciones)
 * 
 * Beneficios:
 * - Interfaz simple y unificada
 * - Clientes no necesitan conocer complejidad interna
 * - Bajo acoplamiento entre cliente y subsistemas
 * - Fácil de usar y de mantener
 */
public class EscrimsFacade {
    private ScrimService scrimService;
    private DomainEventBus eventBus;
    private NotificationSubscriber notificationSubscriber;
    private CommandInvoker commandInvoker;
    private NotifierFactory notifierFactory;
    
    // Adaptadores
    private DiscordAdapter discordAdapter;
    private SendGridAdapter sendgridAdapter;
    private ICalAdapter icalAdapter;
    
    public EscrimsFacade() {
        this(new ByMMRStrategy());
    }
    
    public EscrimsFacade(MatchmakingStrategy initialStrategy) {
        // Inicializar servicios core
        this.scrimService = new ScrimService(initialStrategy);
        this.eventBus = DomainEventBus.getInstance();
        this.notifierFactory = new ProdNotifierFactory();
        this.notificationSubscriber = new NotificationSubscriber(notifierFactory);
        
        // Suscribir al event bus
        this.eventBus.subscribe(notificationSubscriber);
        
        // Inicializar adaptadores
        this.discordAdapter = new DiscordAdapter("webhook_url", "bot_token");
        this.sendgridAdapter = new SendGridAdapter("api_key", "noreply@escrims.com");
        this.icalAdapter = new ICalAdapter("eScrims Calendar");
    }
    
    // ============ OPERACIONES DE USUARIO ============
    
    /**
     * Registra un nuevo usuario en el sistema.
     */
    public Usuario crearUsuario(String username, String email, String password, String region) {
        Usuario usuario = new Usuario(username, email, hashPassword(password), region);
        scrimService.registrarUsuario(usuario);
        notificationSubscriber.registrarUsuario(usuario);
        return usuario;
    }
    
    /**
     * Obtiene estadísticas de un usuario.
     */
    public Map<String, Object> obtenerInfoUsuario(UUID usuarioId) {
        return scrimService.obtenerEstadisticasUsuario(usuarioId);
    }
    
    // ============ OPERACIONES DE SCRIMS ============
    
    /**
     * Crea un nuevo scrim con todos los parámetros.
     */
    public Scrim crearScrim(String juego, String formato, String region,
                           UUID creadorId, String rangoMin, String rangoMax,
                           int latenciaMax, LocalDateTime fechaHora, int cupos, String modalidad) {
        Usuario creador = scrimService.getUsuarioRepository().get(creadorId);
        if (creador == null) {
            throw new IllegalArgumentException("Usuario creador no encontrado");
        }
        
        Scrim scrim = new ScrimBuilder(juego, formato, region, creador)
            .conRangos(rangoMin, rangoMax)
            .conLatenciaMaxima(latenciaMax)
            .conFechaHora(fechaHora)
            .conCupos(cupos)
            .conModalidad(modalidad)
            .build();
        
        // Registrar en servicio para que sea accesible
        scrimService.getScrimRepository().put(scrim.getId(), scrim);
        notificationSubscriber.registrarScrim(scrim);
        return scrim;
    }
    
    /**
     * Busca scrims con filtros avanzados.
     */
    public List<Scrim> buscarScrims(String juego, String formato, String region,
                                     String rangoMin, String rangoMax, int latenciaMax) {
        return scrimService.buscarScrimsAvanzado(juego, formato, region, 
                                                  rangoMin, rangoMax, latenciaMax);
    }
    
    /**
     * Obtiene detalles de un scrim específico.
     */
    public Map<String, Object> obtenerInfoScrim(UUID scrimId) {
        return scrimService.obtenerEstadisticasScrim(scrimId);
    }
    
    // ============ CICLO DE VIDA DEL SCRIM ============
    
    /**
     * Usuario se postula a un scrim.
     */
    public void postularseAScrim(UUID scrimId, UUID usuarioId, String rolDeseado) {
        Usuario usuario = scrimService.getUsuarioRepository().get(usuarioId);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        scrimService.postularseAScrim(scrimId, usuario, rolDeseado);
    }
    
    /**
     * Usuario confirma su participación.
     */
    public void confirmarParticipacion(UUID scrimId, UUID usuarioId) {
        Usuario usuario = scrimService.getUsuarioRepository().get(usuarioId);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        scrimService.confirmarParticipacion(scrimId, usuario);
    }
    
    /**
     * Inicia un scrim.
     */
    public void iniciarScrim(UUID scrimId) {
        scrimService.iniciarScrim(scrimId);
    }
    
    /**
     * Finaliza un scrim.
     */
    public void finalizarScrim(UUID scrimId) {
        Scrim scrim = scrimService.getScrimRepository().get(scrimId);
        if (scrim != null) {
            Estadistica stats = new Estadistica(scrim);
            scrimService.finalizarScrim(scrimId, stats);
        }
    }
    
    /**
     * Cancela un scrim.
     */
    public void cancelarScrim(UUID scrimId) {
        scrimService.cancelarScrim(scrimId);
    }
    
    // ============ ESTRATEGIA DE MATCHMAKING ============
    
    /**
     * Cambia el algoritmo de matchmaking.
     */
    public void cambiarEstrategiaMatchmaking(MatchmakingStrategy strategy) {
        scrimService.setMatchmakingStrategy(strategy);
    }
    
    /**
     * Empareja jugadores para un scrim.
     */
    public List<Usuario> emparejarJugadores(UUID scrimId, List<Usuario> candidatos) {
        Scrim scrim = scrimService.obtenerScrim(scrimId);
        if (scrim == null) {
            throw new IllegalArgumentException("Scrim no encontrado");
        }
        return scrimService.emparejarJugadores(scrim, candidatos);
    }
    
    // ============ COMANDOS (OPERACIONES REVERSIBLES) ============
    
    /**
     * Asigna un rol a un usuario (reversible).
     */
    public void asignarRol(UUID scrimId, UUID usuarioId, String rol) {
        Usuario usuario = scrimService.getUsuarioRepository().get(usuarioId);
        Scrim scrim = scrimService.obtenerScrim(scrimId);
        
        if (usuario == null || scrim == null) {
            throw new IllegalArgumentException("Usuario o Scrim no encontrado");
        }
        
        CommandInvoker invoker = new CommandInvoker(scrim);
        invoker.ejecutar(new AsignarRolCommand(usuario, rol));
    }
    
    /**
     * Intercambia dos jugadores (reversible).
     */
    public void intercambiarJugadores(UUID scrimId, UUID usuarioId1, UUID usuarioId2) {
        Usuario usuario1 = scrimService.getUsuarioRepository().get(usuarioId1);
        Usuario usuario2 = scrimService.getUsuarioRepository().get(usuarioId2);
        Scrim scrim = scrimService.obtenerScrim(scrimId);
        
        if (usuario1 == null || usuario2 == null || scrim == null) {
            throw new IllegalArgumentException("Usuarios o Scrim no encontrado");
        }
        
        CommandInvoker invoker = new CommandInvoker(scrim);
        invoker.ejecutar(new SwapJugadoresCommand(usuario1, usuario2));
    }
    
    /**
     * Invita a un jugador específico (reversible).
     */
    public void invitarJugador(UUID scrimId, UUID usuarioId, String rol) {
        Usuario usuario = scrimService.getUsuarioRepository().get(usuarioId);
        Scrim scrim = scrimService.obtenerScrim(scrimId);
        
        if (usuario == null || scrim == null) {
            throw new IllegalArgumentException("Usuario o Scrim no encontrado");
        }
        
        CommandInvoker invoker = new CommandInvoker(scrim);
        invoker.ejecutar(new InvitarJugadorCommand(usuario, rol));
    }
    
    // ============ MODERACIÓN ============
    
    /**
     * Procesa un reporte de conducta con escalación automática.
     */
    public void reportarConductaInapropiada(UUID scrimId, UUID reportadorId, 
                                            UUID reportadoId, String motivo, String descripcion) {
        Scrim scrim = scrimService.obtenerScrim(scrimId);
        Usuario reportador = scrimService.getUsuarioRepository().get(reportadorId);
        Usuario reportado = scrimService.getUsuarioRepository().get(reportadoId);
        
        if (scrim == null || reportador == null || reportado == null) {
            throw new IllegalArgumentException("Recursos no encontrados");
        }
        
        ReporteConducta reporte = new ReporteConducta(scrim, reportador, reportado, motivo, descripcion);
        scrimService.procesarReporte(reporte);
    }
    
    /**
     * Obtiene la información de sanciones de un usuario.
     */
    public Map<String, Object> obtenerSanciones(UUID usuarioId) {
        Usuario usuario = scrimService.getUsuarioRepository().get(usuarioId);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        
        Map<String, Object> sanciones = new HashMap<>();
        sanciones.put("strikes", usuario.getStrikes());
        sanciones.put("sancionado", usuario.estaBajoSancion());
        sanciones.put("cooldownHasta", usuario.getCooldownHasta());
        return sanciones;
    }
    
    // ============ INTEGRACIONES EXTERNAS ============
    
    /**
     * Conecta con Discord.
     */
    public void conectarDiscord() {
        discordAdapter.conectar();
    }
    
    /**
     * Envía mensaje a Discord.
     */
    public void enviarMensajeDiscord(String canal, String mensaje) {
        discordAdapter.enviarMensaje(canal, mensaje);
    }
    
    /**
     * Conecta con SendGrid para emails.
     */
    public void conectarSendGrid() {
        sendgridAdapter.conectar();
    }
    
    /**
     * Envía email.
     */
    public boolean enviarEmail(String destinatario, String asunto, String cuerpo) {
        return sendgridAdapter.enviarEmail(destinatario, asunto, cuerpo);
    }
    
    /**
     * Conecta con iCal.
     */
    public void conectarICal() {
        icalAdapter.conectar();
    }
    
    /**
     * Exporta evento a calendario.
     */
    public void exportarEventoCalendario(String titulo, LocalDateTime fecha, int duracion) {
        icalAdapter.exportarEvento(titulo, fecha, duracion);
    }
    
    // ============ EVENTOS (OBSERVER) ============
    
    /**
     * Publica un evento de dominio.
     */
    public void publicarEvento(DomainEvent evento) {
        eventBus.publish(evento);
    }
    
    /**
     * Suscribe un observador a eventos.
     */
    public void suscribirse(Subscriber subscriber) {
        eventBus.subscribe(subscriber);
    }
    
    // ============ UTILIDADES ============
    
    /**
     * Obtiene el scrim actual del repositorio.
     */
    public Scrim obtenerScrim(UUID scrimId) {
        return scrimService.obtenerScrim(scrimId);
    }
    
    /**
     * Obtiene el usuario actual del repositorio.
     */
    public Usuario obtenerUsuario(UUID usuarioId) {
        return scrimService.getUsuarioRepository().get(usuarioId);
    }
    
    /**
     * Lista todos los scrims.
     */
    public Collection<Scrim> listarTodosScrims() {
        return scrimService.getScrimRepository().values();
    }
    
    /**
     * Lista todos los usuarios.
     */
    public Collection<Usuario> listarTodosUsuarios() {
        return scrimService.getUsuarioRepository().values();
    }
    
    private String hashPassword(String password) {
        return Integer.toHexString(password.hashCode());
    }
    
    // Getters para acceso a subsistemas si es necesario
    public ScrimService getScrimService() { return scrimService; }
    public DomainEventBus getEventBus() { return eventBus; }
}
