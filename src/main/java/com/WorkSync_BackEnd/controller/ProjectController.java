package com.WorkSync_BackEnd.controller;

import com.WorkSync_BackEnd.domain.service.ProjectService;
import com.WorkSync_BackEnd.domain.model.Project;
import com.WorkSync_BackEnd.domain.model.Assignment;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/proyectos")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping
    public List<Project> getAll() {
        return projectService.getAll();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Project save(@RequestBody Project project) {
        return projectService.save(project);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Project update(@PathVariable Long id, @RequestBody Project project) {
        return projectService.update(id, project);
    }

    @PatchMapping("/{id}/archivar")
    @PreAuthorize("hasRole('ADMIN')")
    public Project archive(@PathVariable Long id) {
        return projectService.archive(id);
    }

    @PostMapping("/{projectId}/colaboradores/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Assignment assignCollaborator(@PathVariable Long projectId, @PathVariable Long userId) {
        return projectService.assignCollaborator(projectId, userId);
    }

    @DeleteMapping("/{projectId}/colaboradores/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public void removeCollaborator(@PathVariable Long projectId, @PathVariable Long userId) {
        projectService.removeCollaborator(projectId, userId);
    }

    @GetMapping("/{id}/metricas")
    public com.WorkSync_BackEnd.domain.dto.ProjectMetricsDTO getMetrics(@PathVariable Long id) {
        return projectService.getMetrics(id);
    }

    @GetMapping("/metricas/globales")
    @PreAuthorize("hasRole('ADMIN')")
    public com.WorkSync_BackEnd.domain.dto.GlobalMetricsDTO getGlobalMetrics() {
        return projectService.getGlobalMetrics();
    }
}
