package com.WorkSync_BackEnd.controller;

import com.WorkSync_BackEnd.domain.dto.TaskResponseDTO;
import com.WorkSync_BackEnd.domain.model.Task;
import com.WorkSync_BackEnd.domain.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tareas") // Keep the endpoint Spanish for Frontend mapping
@RequiredArgsConstructor
@CrossOrigin("*")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponseDTO> create(@RequestBody Task task) {
        return new ResponseEntity<>(taskService.create(task), HttpStatus.CREATED);
    }

    @GetMapping("/proyecto/{projectId}")
    public ResponseEntity<List<TaskResponseDTO>> getByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(taskService.getByProject(projectId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> update(@PathVariable Long id, @RequestBody Task taskDetails) {
        return ResponseEntity.ok(taskService.update(id, taskDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody java.util.Map<String, String> body) {
        try {
            com.WorkSync_BackEnd.persistence.entity.enums.EstadoTarea newStatus = 
                com.WorkSync_BackEnd.persistence.entity.enums.EstadoTarea.valueOf(body.get("status"));
            return ResponseEntity.ok(taskService.updateStatus(id, newStatus));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor");
        }
    }
}
