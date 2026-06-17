package com.escrims.application.controller;

import com.escrims.application.service.ScrimService;
import com.escrims.domain.model.Estadistica;
import com.escrims.domain.model.Scrim;
import com.escrims.domain.model.Usuario;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ScrimController {
    private final ScrimService scrimService;

    public ScrimController(ScrimService scrimService) {
        this.scrimService = scrimService;
    }

    public List<Scrim> buscarScrims(String juego, String region, String rangoMin,
                                    String rangoMax, String formato, int latenciaMax) {
        return scrimService.buscarScrimsAvanzado(juego, formato, region,
            rangoMin, rangoMax, latenciaMax);
    }

    public List<Scrim> buscarScrims(Map<String, Object> filtros) {
        return buscarScrims(
            (String) filtros.get("juego"),
            (String) filtros.get("region"),
            (String) filtros.get("rangoMin"),
            (String) filtros.get("rangoMax"),
            (String) filtros.get("formato"),
            (int) filtros.getOrDefault("latenciaMax", 0)
        );
    }

    public Map<String, Object> obtenerDetallesScrim(UUID scrimId) {
        Scrim scrim = scrimService.obtenerScrim(scrimId);
        if (scrim == null) {
            return crearError(404, "Scrim no encontrado");
        }
        return scrimService.obtenerEstadisticasScrim(scrimId);
    }

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
                rangoMin, rangoMax, latenciaMax, fechaHora, cupos, modalidad);

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

    public Scrim crearScrim(UUID creadorId, Map<String, Object> datos) {
        Usuario creador = scrimService.getUsuarioRepository().get(creadorId);
        if (creador == null) {
            throw new IllegalArgumentException("Usuario creador no encontrado");
        }
        return scrimService.crearScrim(
            (String) datos.get("juego"),
            (String) datos.get("formato"),
            (String) datos.get("region"),
            creador,
            (String) datos.get("rangoMin"),
            (String) datos.get("rangoMax"),
            (int) datos.getOrDefault("latenciaMax", 0),
            (LocalDateTime) datos.get("fechaHora"),
            (int) datos.getOrDefault("cupos", 10),
            (String) datos.getOrDefault("modalidad", "casual")
        );
    }

    public Map<String, Object> postularseAScrim(UUID scrimId, UUID usuarioId, String rolDeseado) {
        try {
            Usuario usuario = scrimService.getUsuarioRepository().get(usuarioId);
            if (usuario == null) {
                return crearError(404, "Usuario no encontrado");
            }
            scrimService.postularseAScrim(scrimId, usuario, rolDeseado);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Postulacion registrada");
            response.put("usuario", usuario.getUsername());
            response.put("rol", rolDeseado);
            return response;
        } catch (Exception e) {
            return crearError(400, e.getMessage());
        }
    }

    public Map<String, Object> confirmarParticipacion(UUID scrimId, UUID usuarioId) {
        try {
            Usuario usuario = scrimService.getUsuarioRepository().get(usuarioId);
            if (usuario == null) {
                return crearError(404, "Usuario no encontrado");
            }
            scrimService.confirmarParticipacion(scrimId, usuario);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Participacion confirmada");
            response.put("usuario", usuario.getUsername());
            return response;
        } catch (Exception e) {
            return crearError(400, e.getMessage());
        }
    }

    public Map<String, Object> iniciarScrim(UUID scrimId) {
        try {
            scrimService.iniciarScrim(scrimId);
            return crearExito("Scrim iniciado");
        } catch (Exception e) {
            return crearError(400, e.getMessage());
        }
    }

    public Map<String, Object> finalizarScrim(UUID scrimId) {
        try {
            Scrim scrim = scrimService.getScrimRepository().get(scrimId);
            if (scrim == null) {
                return crearError(404, "Scrim no encontrado");
            }
            scrimService.finalizarScrim(scrimId, new Estadistica(scrim));
            return crearExito("Scrim finalizado");
        } catch (Exception e) {
            return crearError(400, e.getMessage());
        }
    }

    public Map<String, Object> cancelarScrim(UUID scrimId) {
        try {
            scrimService.cancelarScrim(scrimId);
            return crearExito("Scrim cancelado");
        } catch (Exception e) {
            return crearError(400, e.getMessage());
        }
    }

    public Map<String, Object> cargarEstadisticas(UUID scrimId, Map<String, Object> estadisticas) {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Estadisticas registradas");
            response.put("scrim_id", scrimId);
            return response;
        } catch (Exception e) {
            return crearError(400, e.getMessage());
        }
    }

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

    public Map<String, Object> ejecutarComando(UUID scrimId, String comando) {
        return ejecutarComando(scrimId, comando, Collections.emptyMap());
    }

    public Map<String, Object> obtenerEstadisticasUsuario(UUID usuarioId) {
        return scrimService.obtenerEstadisticasUsuario(usuarioId);
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
