package com.escrims.application;

import com.escrims.domain.model.*;
import com.escrims.domain.moderacion.*;
import com.escrims.domain.strategy.MatchmakingStrategy;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * CAPA DE APLICACIÓN - Service
 * Orquesta casos de uso relacionados con Scrims.
 * Aplica principio GRASP de Controller y Expert.
 */
public class ScrimService {
    private final Map<UUID, Scrim> scrimRepository;
    private final Map<UUID, Usuario> usuarioRepository;
    private MatchmakingStrategy matchmakingStrategy;
    
    public ScrimService(MatchmakingStrategy matchmakingStrategy) {
        this.scrimRepository = new HashMap<>();
        this.usuarioRepository = new HashMap<>();
        this.matchmakingStrategy = matchmakingStrategy;
    }
    
    /**
     * CU3 - Crear Scrim
     * Aplica GRASP Creator: ScrimService crea Scrim porque tiene los datos necesarios
     */
    public Scrim crearScrim(String juego, String formato, String region, 
                           Usuario creador, String rangoMin, String rangoMax,
                           int latenciaMax, LocalDateTime fechaHora, 
                           int cuposTotales, String modalidad) {
        
        Scrim scrim = new Scrim(juego, formato, region, creador);
        scrim.setRangoMin(rangoMin);
        scrim.setRangoMax(rangoMax);
        scrim.setLatenciaMax(latenciaMax);
        scrim.setFechaHora(fechaHora);
        scrim.setCuposTotales(cuposTotales);
        scrim.setModalidad(modalidad);
        
        scrimRepository.put(scrim.getId(), scrim);
        return scrim;
    }
    
    /**
     * CU4 - Postularse a Scrim
     * Aplica GRASP Controller: coordina la operación
     * Automáticamente ejecuta matchmaking cuando se completa el cupo
     */
    public void postularseAScrim(UUID scrimId, Usuario usuario, String rolDeseado) {
        Scrim scrim = scrimRepository.get(scrimId);
        if (scrim == null) {
            throw new IllegalArgumentException("Scrim no encontrado");
        }
        
        // Delega al State la lógica de postulación
        scrim.postular(usuario, rolDeseado);
        
        // Si se completó el cupo, ejecutar matchmaking automático
        if (scrim.estaCompleto() && scrim.getEquipoA().getJugadores().isEmpty()) {
            List<Usuario> postulados = scrim.getPostulaciones().stream()
                .filter(p -> p.getEstado() == EstadoPostulacion.ACEPTADA)
                .map(Postulacion::getUsuario)
                .toList();
            
            List<Usuario> seleccionados = matchmakingStrategy.seleccionar(postulados, scrim);
            scrim.ejecutarMatchmaking(seleccionados);
            
            System.out.println("[MATCHMAKING] Automático ejecutado. Equipos armados.");
        }
    }
    
    /**
     * CU5 - Emparejar jugadores
     * Aplica GRASP Expert: usa Strategy para delegar el algoritmo
     */
    public List<Usuario> emparejarJugadores(Scrim scrim, List<Usuario> candidatos) {
        return matchmakingStrategy.seleccionar(candidatos, scrim);
    }
    
    /**
     * CU6 - Confirmar participación
     */
    public void confirmarParticipacion(UUID scrimId, Usuario usuario) {
        Scrim scrim = scrimRepository.get(scrimId);
        if (scrim == null) {
            throw new IllegalArgumentException("Scrim no encontrado");
        }
        
        scrim.confirmar(usuario);
    }
    
    /**
     * CU7 - Iniciar Scrim
     */
    public void iniciarScrim(UUID scrimId) {
        Scrim scrim = scrimRepository.get(scrimId);
        if (scrim == null) {
            throw new IllegalArgumentException("Scrim no encontrado");
        }
        
        scrim.iniciar();
    }
    
    /**
     * CU8 - Finalizar y cargar estadísticas
     */
    public void finalizarScrim(UUID scrimId, Estadistica estadistica) {
        Scrim scrim = scrimRepository.get(scrimId);
        if (scrim == null) {
            throw new IllegalArgumentException("Scrim no encontrado");
        }
        
        scrim.finalizar();
        scrim.setEstadistica(estadistica);
    }
    
    /**
     * CU9 - Cancelar Scrim
     */
    public void cancelarScrim(UUID scrimId) {
        Scrim scrim = scrimRepository.get(scrimId);
        if (scrim == null) {
            throw new IllegalArgumentException("Scrim no encontrado");
        }
        
        scrim.cancelar();
    }
    
    /**
     * CU2 - Buscar Scrims
     * Aplica GRASP Expert: ScrimService conoce todos los scrims
     */
    public List<Scrim> buscarScrims(String juego, String region, 
                                    String rangoMin, String rangoMax) {
        List<Scrim> resultados = new ArrayList<>();
        
        for (Scrim scrim : scrimRepository.values()) {
            if (scrim.getJuego().equals(juego) && 
                scrim.getRegion().equals(region) &&
                scrim.getNombreEstado().equals("Buscando Jugadores")) {
                resultados.add(scrim);
            }
        }
        
        return resultados;
    }
    
    // Cambiar estrategia en tiempo de ejecución (Strategy Pattern)
    public void setMatchmakingStrategy(MatchmakingStrategy strategy) {
        this.matchmakingStrategy = strategy;
    }
    
    public void registrarUsuario(Usuario usuario) {
        usuarioRepository.put(usuario.getId(), usuario);
    }
    
    public Scrim obtenerScrim(UUID scrimId) {
        return scrimRepository.get(scrimId);
    }
    
    /**
     * CU11 - Moderar reportes (Chain of Responsibility Pattern)
     */
    public void procesarReporte(ReporteConducta reporte) {
        // Crear cadena de procesadores
        ReportProcessor auto = new AutomaticProcessor();
        ReportProcessor bot = new BotProcessor();
        ReportProcessor human = new HumanModeratorProcessor();
        
        // Conectar cadena
        auto.setSiguiente(bot);
        bot.setSiguiente(human);
        
        // Procesar
        auto.procesarReporte(reporte);
        
        // Aplicar sanciones al usuario
        if (reporte.getSancion() != ReporteConducta.Sancion.NINGUNA) {
            aplicarSancion(reporte.getReportado(), reporte.getSancion());
        }
    }
    
    private void aplicarSancion(Usuario usuario, ReporteConducta.Sancion sancion) {
        switch (sancion) {
            case ADVERTENCIA:
                usuario.aplicarStrike();
                System.out.println("[SERVICE] Strike aplicado a " + usuario.getUsername());
                break;
            case SUSPENSION_24H:
            case SUSPENSION_7D:
            case BAN_PERMANENTE:
                usuario.setStrikes(usuario.getStrikes() + 2);
                System.out.println("[SERVICE] Usuario " + usuario.getUsername() + " sancionado: " + sancion);
                break;
            default:
                break;
        }
    }
    
    /**
     * Obtener scrims disponibles con filtros avanzados
     */
    public List<Scrim> buscarScrimsAvanzado(String juego, String formato, String region, 
                                            String rangoMin, String rangoMax, 
                                            int latenciaMax) {
        return scrimRepository.values().stream()
            .filter(s -> juego == null || s.getJuego().equalsIgnoreCase(juego))
            .filter(s -> formato == null || s.getFormato().equalsIgnoreCase(formato))
            .filter(s -> region == null || s.getRegion().equalsIgnoreCase(region))
            .filter(s -> rangoMin == null || s.getRangoMin().equalsIgnoreCase(rangoMin))
            .filter(s -> rangoMax == null || s.getRangoMax().equalsIgnoreCase(rangoMax))
            .filter(s -> s.getLatenciaMax() >= latenciaMax)
            .filter(s -> s.getNombreEstado().equals("Buscando Jugadores"))
            .collect(Collectors.toList());
    }
    
    /**
     * Obtener estadísticas de un usuario
     */
    public Map<String, Object> obtenerEstadisticasUsuario(UUID usuarioId) {
        Map<String, Object> stats = new HashMap<>();
        
        Usuario usuario = usuarioRepository.get(usuarioId);
        if (usuario != null) {
            stats.put("username", usuario.getUsername());
            stats.put("region", usuario.getRegion());
            stats.put("strikes", usuario.getStrikes());
            stats.put("sancionado", usuario.estaBajoSancion());
            stats.put("rangos", usuario.getRangoPorJuego());
            stats.put("rolesPreferidos", usuario.getRolesPreferidos());
        }
        
        return stats;
    }
    
    /**
     * Obtener estadísticas de un scrim
     */
    public Map<String, Object> obtenerEstadisticasScrim(UUID scrimId) {
        Map<String, Object> stats = new HashMap<>();
        
        Scrim scrim = scrimRepository.get(scrimId);
        if (scrim != null) {
            stats.put("juego", scrim.getJuego());
            stats.put("formato", scrim.getFormato());
            stats.put("estado", scrim.getNombreEstado());
            stats.put("postulados", scrim.getPostulaciones().size());
            stats.put("cuposTotales", scrim.getCuposTotales());
            stats.put("confirmados", scrim.getConfirmaciones().size());
            stats.put("creador", scrim.getCreador().getUsername());
        }
        
        return stats;
    }
    
    // Getters auxiliares
    public Map<UUID, Scrim> getScrimRepository() { return scrimRepository; }
    public Map<UUID, Usuario> getUsuarioRepository() { return usuarioRepository; }
}
