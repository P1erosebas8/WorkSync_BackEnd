package com.WorkSync_BackEnd.controller;

import com.WorkSync_BackEnd.domain.dto.AsignacionRequestDTO;
import com.WorkSync_BackEnd.domain.service.AssignmentService;
import com.WorkSync_BackEnd.domain.model.Assignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asignaciones") // Mantenemos la ruta en español para no romper URLs del frontend de momento
@CrossOrigin("*")
public class AssignmentController {

    @Autowired
    private AssignmentService assignmentService;

    @PostMapping
    public ResponseEntity<Assignment> assignUserToProject(@RequestBody AsignacionRequestDTO dto) {
        Assignment assignment = assignmentService.assignUserToProject(dto);
        return ResponseEntity.ok(assignment);
    }

    @GetMapping("/proyecto/{projectId}")
    public ResponseEntity<List<Assignment>> getAssignmentsByProject(@PathVariable Long projectId) {
        List<Assignment> assignments = assignmentService.getAssignmentsByProject(projectId);
        return ResponseEntity.ok(assignments);
    }
}
