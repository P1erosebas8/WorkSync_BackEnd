package com.WorkSync_BackEnd.domain.service;

import com.WorkSync_BackEnd.domain.dto.TaskResponseDTO;
import com.WorkSync_BackEnd.domain.model.Task;
import com.WorkSync_BackEnd.domain.repository.TaskRepository;
import com.WorkSync_BackEnd.exception.ResourceNotFoundException;
import com.WorkSync_BackEnd.persistence.entity.enums.EstadoTarea;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskResponseDTO create(Task task) {
        task.setStatus(EstadoTarea.PENDIENTE);
        task.setProgressPercentage(0);
        Task saved = taskRepository.save(task);
        return toDto(saved);
    }

    public List<TaskResponseDTO> getByProject(Long projectId) {
        return taskRepository.getByProject(projectId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public TaskResponseDTO getById(Long id) {
        return taskRepository.getById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada"));
    }

    public TaskResponseDTO update(Long id, Task taskDetails) {
        Task task = taskRepository.getById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada"));
                
        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setStatus(taskDetails.getStatus());
        task.setPriority(taskDetails.getPriority());
        task.setDueDate(taskDetails.getDueDate());
        task.setProgressPercentage(taskDetails.getProgressPercentage());
        
        if (taskDetails.getAssigneeId() != null) {
            task.setAssigneeId(taskDetails.getAssigneeId());
        }

        Task saved = taskRepository.save(task);
        return toDto(saved);
    }

    public void delete(Long id) {
        if (!taskRepository.getById(id).isPresent()) {
            throw new ResourceNotFoundException("Tarea no encontrada");
        }
        taskRepository.delete(id);
    }

    public TaskResponseDTO updateStatus(Long id, EstadoTarea newStatus) {
        Task task = taskRepository.getById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada"));
                
        if (task.getDependsOnTaskId() != null && newStatus != EstadoTarea.PENDIENTE) {
            Task parentTask = taskRepository.getById(task.getDependsOnTaskId())
                    .orElseThrow(() -> new ResourceNotFoundException("Dependencia no encontrada"));
            if (parentTask.getStatus() != EstadoTarea.COMPLETADO) {
                throw new IllegalStateException("No se puede mover la tarea porque depende de otra que no ha sido completada.");
            }
        }
        
        if (task.getStatus() == EstadoTarea.COMPLETADO && newStatus != EstadoTarea.COMPLETADO) {
            org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ADMIN"));
            if (!isAdmin) {
                throw new IllegalStateException("Solo un administrador puede devolver una tarea completada (Control de Calidad).");
            }
        }
                
        task.setStatus(newStatus);
        Task saved = taskRepository.save(task);
        return toDto(saved);
    }

    private TaskResponseDTO toDto(Task task) {
        return TaskResponseDTO.builder()
                .taskId(task.getTaskId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .progressPercentage(task.getProgressPercentage())
                .projectId(task.getProjectId())
                .assigneeId(task.getAssigneeId())
                .assigneeName(task.getAssigneeName())
                .dependsOnTaskId(task.getDependsOnTaskId())
                .build();
    }
}
