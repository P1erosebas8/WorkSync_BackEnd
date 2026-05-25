package com.WorkSync_BackEnd.controller;

import com.WorkSync_BackEnd.domain.service.ProyectoService;
import com.WorkSync_BackEnd.persistence.entity.Proyecto;
import com.WorkSync_BackEnd.persistence.entity.Asignacion;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/proyectos")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ProyectoController {
    private final ProyectoService proyectoService;

    @GetMapping
    public List<Proyecto> listar() {
        return proyectoService.listar();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Proyecto guardar(@RequestBody Proyecto proyecto) {
        return proyectoService.guardar(proyecto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Proyecto editar(@PathVariable Long id, @RequestBody Proyecto proyecto) {
        return proyectoService.editar(id, proyecto);
    }

    @PatchMapping("/{id}/archivar")
    @PreAuthorize("hasRole('ADMIN')")
    public Proyecto archivar(@PathVariable Long id) {
        return proyectoService.archivar(id);
    }

    @PostMapping("/{idProyecto}/colaboradores/{idUsuario}")
    @PreAuthorize("hasRole('ADMIN')")
    public Asignacion asignarColaborador(@PathVariable Long idProyecto, @PathVariable Long idUsuario) {
        return proyectoService.asignarColaborador(idProyecto, idUsuario);
    }

    @DeleteMapping("/{idProyecto}/colaboradores/{idUsuario}")
    @PreAuthorize("hasRole('ADMIN')")
    public void removerColaborador(@PathVariable Long idProyecto, @PathVariable Long idUsuario) {
        proyectoService.removerColaborador(idProyecto, idUsuario);
    }
}
