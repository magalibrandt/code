package com.escrims.application.service;

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
        
        Scrim scrim = crearScrim(creador, juego, formato, region);
        scrim.setRangoMin(rangoMin);
        scrim.setRangoMax(rangoMax);
        scrim.setLatenciaMax(latenciaMax);
        scrim.setFechaHora(fechaHora);
        scrim.setCuposTotales(cuposTotales);
        scrim.setModalidad(modalidad);
        return scrim;
    }

    public Scrim crearScrim(Usuario creador, String juego, String formato, String region) {
        Scrim scrim = new Scrim(creador, juego, formato, region);
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
                .filter(p -> p.getEstado().esAceptada())
                .map(Postulacion::getUsuario)
                .toList();
            
            List<Usuario> seleccionados = matchmakingStrategy.seleccionar(postulados, scrim);
            if (seleccionados.size() < scrim.getCuposTotales()) {
                throw new IllegalStateException("Matchmaking insuficiente: seleccionados "
                    + seleccionados.size() + " de " + scrim.getCuposTotales());
            }
            scrim.ejecutarMatchmaking(seleccionados);
            
            System.out.println("[MATCHMAKING] Automático ejecutado. Equipos armados.");
        }
    }
    
    public void postularseAScrim(UUID scrimId, UUID usuarioId, String rolDeseado) {
        Usuario usuario = usuarioRepository.get(usuarioId);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        postularseAScrim(scrimId, usuario, rolDeseado);
    }

    public void emparejarYArmarLobby(UUID scrimId) {
        Scrim scrim = scrimRepository.get(scrimId);
        if (scrim == null) {
            throw new IllegalArgumentException("Scrim no encontrado");
        }
        List<Usuario> postulados = scrim.getPostulaciones().stream()
            .filter(p -> p.getEstado().esAceptada())
            .map(Postulacion::getUsuario)
            .toList();
        List<Usuario> seleccionados = matchmakingStrategy.seleccionar(postulados, scrim);
        scrim.ejecutarMatchmaking(seleccionados);
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
    
    public void confirmarParticipacion(UUID scrimId, UUID usuarioId) {
        Usuario usuario = usuarioRepository.get(usuarioId);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        confirmarParticipacion(scrimId, usuario);
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
    
    public void finalizarScrim(UUID scrimId) {
        Scrim scrim = scrimRepository.get(scrimId);
        if (scrim == null) {
            throw new IllegalArgumentException("Scrim no encontrado");
        }
        finalizarScrim(scrimId, new Estadistica(scrim));
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
        if (!reporte.getSancion().esNinguna()) {
            aplicarSancion(reporte.getReportado(), reporte.getSancion());
        }
    }
    
    private void aplicarSancion(Usuario usuario, Sancion sancion) {
        if (sancion.esAdvertencia()) {
            usuario.aplicarStrike();
            System.out.println("[SERVICE] Strike aplicado a " + usuario.getUsername());
            return;
        }
        if (sancion.requiereCooldown()) {
            usuario.setStrikes(usuario.getStrikes() + 2);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, sancion.getDuracionDias());
            usuario.setCooldownHasta(cal.getTime());
            System.out.println("[SERVICE] Usuario " + usuario.getUsername() + " sancionado: " + sancion.getNombre());
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
            .filter(s -> rangoMin == null || (s.getRangoMin() != null && s.getRangoMin().equalsIgnoreCase(rangoMin)))
            .filter(s -> rangoMax == null || (s.getRangoMax() != null && s.getRangoMax().equalsIgnoreCase(rangoMax)))
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
