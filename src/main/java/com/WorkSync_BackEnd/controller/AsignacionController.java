package com.WorkSync_BackEnd.controller;

import com.WorkSync_BackEnd.domain.dto.AsignacionRequestDTO;
import com.WorkSync_BackEnd.domain.service.AsignacionService;
import com.WorkSync_BackEnd.persistence.entity.Asignacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asignaciones")
@CrossOrigin("*")
public class AsignacionController {

    @Autowired
    private AsignacionService asignacionService;

    @PostMapping
    public ResponseEntity<Asignacion> asignarUsuarioAProyecto(@RequestBody AsignacionRequestDTO dto) {
        Asignacion asignacion = asignacionService.asignarUsuarioAProyecto(dto);
        return ResponseEntity.ok(asignacion);
    }

    @GetMapping("/proyecto/{idProyecto}")
    public ResponseEntity<List<Asignacion>> obtenerAsignacionesDeProyecto(@PathVariable Long idProyecto) {
        List<Asignacion> asignaciones = asignacionService.obtenerAsignacionesPorProyecto(idProyecto);
        return ResponseEntity.ok(asignaciones);
    }
}
