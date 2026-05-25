package com.WorkSync_BackEnd.controller;

import com.WorkSync_BackEnd.domain.service.TareaService;
import com.WorkSync_BackEnd.persistence.entity.Tarea;
import com.WorkSync_BackEnd.persistence.entity.enums.EstadoTarea;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tareas")
@RequiredArgsConstructor
@CrossOrigin("*")
public class TareaController {
    private final TareaService tareaService;

    @GetMapping
    public List<Tarea> listar() {
        return tareaService.listar();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Tarea guardar(@RequestBody Tarea tarea) {
        return tareaService.guardar(tarea);
    }

    @GetMapping("/proyecto/{idProyecto}")
    public List<Tarea> listarPorProyecto(@PathVariable Long idProyecto) {
        return tareaService.listarPorProyecto(idProyecto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Tarea editar(@PathVariable Long id, @RequestBody Tarea tarea) {
        return tareaService.editar(id, tarea);
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN', 'COLABORADOR')")
    public Tarea actualizarEstado(@PathVariable Long id, @RequestBody Map<String, String> body) {
        EstadoTarea nuevoEstado = EstadoTarea.valueOf(body.get("estado"));
        return tareaService.actualizarEstado(id, nuevoEstado);
    }
}
