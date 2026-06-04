package com.escrims.application;

import com.escrims.domain.model.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * CAPA DE PRESENTACIÓN - REST Controller (Simulado)
 * Define los endpoints de la API REST del sistema eScrims.
 * 
 * En un proyecto real, esto usaría Spring Boot:
 * @RestController
 * @RequestMapping("/api")
 */
public class ScrimController {
    private ScrimService scrimService;
    
    public ScrimController(ScrimService scrimService) {
        this.scrimService = scrimService;
    }
    
    // ============ AUTENTICACIÓN ============
    
    /**
     * POST /api/auth/register
     * CU1: Registrar usuario
     */
    public Usuario registrarUsuario(String username, String email, String password, String region) {
        // En producción: hashear password
        String passwordHash = hashPassword(password);
        return new Usuario(username, email, passwordHash, region);
    }
    
    /**
     * POST /api/auth/login
     * CU2: Autenticar usuario
     */
    public Map<String, Object> autenticar(String email, String password) {
        Map<String, Object> response = new HashMap<>();
        // En un caso real, buscar usuario en BD y validar
        response.put("token", "jwt_token_aqui");
        response.put("usuario_id", UUID.randomUUID());
        response.put("mensaje", "Login exitoso");
        return response;
    }
    
    // ============ BÚSQUEDA DE SCRIMS ============
    
    /**
     * GET /api/scrims
     * Parámetros query: juego, region, rangoMin, rangoMax, formato, latenciaMax
     * CU2: Búsqueda avanzada de scrims
     */
    public List<Scrim> buscarScrims(String juego, String region, String rangoMin, 
                                     String rangoMax, String formato, int latenciaMax) {
        return scrimService.buscarScrimsAvanzado(juego, formato, region, 
                                                  rangoMin, rangoMax, latenciaMax);
    }
    
    /**
     * GET /api/scrims/{id}
     * Obtener detalles de un scrim específico
     */
    public Map<String, Object> obtenerDetallesScrim(UUID scrimId) {
        Scrim scrim = scrimService.obtenerScrim(scrimId);
        if (scrim == null) {
            return crearError(404, "Scrim no encontrado");
        }
        return scrimService.obtenerEstadisticasScrim(scrimId);
    }
    
    // ============ CREACIÓN Y GESTIÓN DE SCRIMS ============
    
    /**
     * POST /api/scrims
     * CU3: Crear scrim
     */
    public Map<String, Object> crearScrim(String juego, String formato, String region,
                                          UUID creadorId, String rangoMin, String rangoMax,
                                          int latenciaMax, LocalDateTime fechaHora, 
                                          int cupos, String modalidad) {
        try {
            Usuario creador = scrimService.getUsuarioRepository().get(creadorId);
            if (creador == null) {
                return crearError(404, "Usuario creador no encontrado");
            }
            
            Scrim scrim = scrimService.crearScrim(juego, formato, region, creador,
                                                   rangoMin, rangoMax, latenciaMax,
                                                   fechaHora, cupos, modalidad);
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", scrim.getId());
            response.put("juego", scrim.getJuego());
            response.put("estado", scrim.getNombreEstado());
            response.put("mensaje", "Scrim creado exitosamente");
            return response;
        } catch (Exception e) {
            return crearError(500, "Error al crear scrim: " + e.getMessage());
        }
    }
    
    /**
     * POST /api/scrims/{id}/postulaciones
     * CU4: Postularse a scrim
     */
    public Map<String, Object> postularseAScrim(UUID scrimId, UUID usuarioId, String rolDeseado) {
        try {
            Usuario usuario = scrimService.getUsuarioRepository().get(usuarioId);
            if (usuario == null) {
                return crearError(404, "Usuario no encontrado");
            }
            
            scrimService.postularseAScrim(scrimId, usuario, rolDeseado);
            
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Postulación registrada");
            response.put("usuario", usuario.getUsername());
            response.put("rol", rolDeseado);
            return response;
        } catch (Exception e) {
            return crearError(400, e.getMessage());
        }
    }
    
    /**
     * POST /api/scrims/{id}/confirmaciones
     * CU6: Confirmar participación
     */
    public Map<String, Object> confirmarParticipacion(UUID scrimId, UUID usuarioId) {
        try {
            Usuario usuario = scrimService.getUsuarioRepository().get(usuarioId);
            if (usuario == null) {
                return crearError(404, "Usuario no encontrado");
            }
            
            scrimService.confirmarParticipacion(scrimId, usuario);
            
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Participación confirmada");
            response.put("usuario", usuario.getUsername());
            return response;
        } catch (Exception e) {
            return crearError(400, e.getMessage());
        }
    }
    
    /**
     * POST /api/scrims/{id}/iniciar
     * CU7: Iniciar scrim
     */
    public Map<String, Object> iniciarScrim(UUID scrimId) {
        try {
            scrimService.iniciarScrim(scrimId);
            return crearExito("Scrim iniciado");
        } catch (Exception e) {
            return crearError(400, e.getMessage());
        }
    }
    
    /**
     * POST /api/scrims/{id}/finalizar
     * CU8: Finalizar scrim
     */
    public Map<String, Object> finalizarScrim(UUID scrimId) {
        try {
            // Obtener scrim para crear estadística
            Scrim scrim = scrimService.getScrimRepository().get(scrimId);
            if (scrim == null) {
                return crearError(404, "Scrim no encontrado");
            }
            
            // En un caso real, recibir estadísticas del body
            Estadistica stats = new Estadistica(scrim);
            scrimService.finalizarScrim(scrimId, stats);
            return crearExito("Scrim finalizado");
        } catch (Exception e) {
            return crearError(400, e.getMessage());
        }
    }
    
    /**
     * POST /api/scrims/{id}/cancelar
     * CU9: Cancelar scrim
     */
    public Map<String, Object> cancelarScrim(UUID scrimId) {
        try {
            scrimService.cancelarScrim(scrimId);
            return crearExito("Scrim cancelado");
        } catch (Exception e) {
            return crearError(400, e.getMessage());
        }
    }
    
    /**
     * POST /api/scrims/{id}/estadisticas
     * CU8: Cargar estadísticas finales
     */
    public Map<String, Object> cargarEstadisticas(UUID scrimId, Map<String, Object> estadisticas) {
        try {
            // En un caso real, parsear y validar estadísticas
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Estadísticas registradas");
            response.put("scrim_id", scrimId);
            return response;
        } catch (Exception e) {
            return crearError(400, e.getMessage());
        }
    }
    
    // ============ OPERACIONES CON COMANDOS ============
    
    /**
     * POST /api/scrims/{id}/acciones/{command}
     * Soporta: asignar-rol, swap-jugadores, invitar-jugador
     */
    public Map<String, Object> ejecutarComando(UUID scrimId, String comando, Map<String, Object> params) {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("comando", comando);
            response.put("estado", "ejecutado");
            return response;
        } catch (Exception e) {
            return crearError(400, e.getMessage());
        }
    }
    
    // ============ INFORMACIÓN DE USUARIO ============
    
    /**
     * GET /api/usuarios/{id}/estadisticas
     * Obtener estadísticas de un usuario
     */
    public Map<String, Object> obtenerEstadisticasUsuario(UUID usuarioId) {
        return scrimService.obtenerEstadisticasUsuario(usuarioId);
    }
    
    // ============ UTILIDADES ============
    
    private String hashPassword(String password) {
        // En producción: usar bcrypt o similar
        return Integer.toHexString(password.hashCode());
    }
    
    private Map<String, Object> crearExito(String mensaje) {
        Map<String, Object> response = new HashMap<>();
        response.put("exito", true);
        response.put("mensaje", mensaje);
        return response;
    }
    
    private Map<String, Object> crearError(int codigo, String mensaje) {
        Map<String, Object> response = new HashMap<>();
        response.put("exito", false);
        response.put("codigo", codigo);
        response.put("error", mensaje);
        return response;
    }
}
