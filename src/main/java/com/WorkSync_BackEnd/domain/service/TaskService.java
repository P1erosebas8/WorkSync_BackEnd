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
    private final com.WorkSync_BackEnd.domain.repository.TaskHistoryRepository taskHistoryRepository;
    private final com.WorkSync_BackEnd.domain.repository.UserRepository userRepository;
    private final TaskNotificationService taskNotificationService;

    private Long getCurrentUserId() {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails) {
            String username = ((org.springframework.security.core.userdetails.UserDetails) auth.getPrincipal()).getUsername();
            return userRepository.getByEmail(username)
                    .map(com.WorkSync_BackEnd.domain.model.User::getUserId).orElse(null);
        }
        return null;
    }

    private void recordHistory(Long taskId, String oldStatus, String newStatus, String detail) {
        Long currentUserId = getCurrentUserId();
        com.WorkSync_BackEnd.domain.model.TaskHistory history = com.WorkSync_BackEnd.domain.model.TaskHistory.builder()
                .taskId(taskId)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .changeDetail(detail)
                .changeDate(java.time.LocalDateTime.now())
                .userId(currentUserId)
                .build();
        taskHistoryRepository.save(history);
    }

    public TaskResponseDTO create(Task task) {
        task.setStatus(EstadoTarea.PENDIENTE);
        task.setProgressPercentage(0);
        Task saved = taskRepository.save(task);
        
        if (saved.getAssigneeId() != null) {
            taskNotificationService.notifyReassignment(saved);
        }
        
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
                
        String oldStatus = task.getStatus().name();
        String oldAssignee = String.valueOf(task.getAssigneeId());
        
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
        
        if (!oldStatus.equals(saved.getStatus().name())) {
            recordHistory(saved.getTaskId(), oldStatus, saved.getStatus().name(), "Cambio de estado en edición");
            taskNotificationService.notifyStatusChange(saved, oldStatus);
        }
        if (!String.valueOf(saved.getAssigneeId()).equals(oldAssignee)) {
            recordHistory(saved.getTaskId(), oldStatus, saved.getStatus().name(), "Reasignación de tarea");
            taskNotificationService.notifyReassignment(saved);
        }
        
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
                
        String oldStatus = task.getStatus().name();
        task.setStatus(newStatus);
        Task saved = taskRepository.save(task);
        
        recordHistory(saved.getTaskId(), oldStatus, newStatus.name(), "Movimiento en Kanban");
        taskNotificationService.notifyStatusChange(saved, oldStatus);
        
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

    public List<com.WorkSync_BackEnd.domain.dto.TaskHistoryDTO> getHistory(Long taskId) {
        return taskHistoryRepository.getByTaskId(taskId).stream().map(h -> {
            String userName = "Sistema";
            if (h.getUserId() != null) {
                userName = userRepository.getById(h.getUserId())
                        .map(com.WorkSync_BackEnd.domain.model.User::getName)
                        .orElse("Usuario Desconocido");
            }
            return com.WorkSync_BackEnd.domain.dto.TaskHistoryDTO.builder()
                    .historyId(h.getHistoryId())
                    .taskId(h.getTaskId())
                    .oldStatus(h.getOldStatus())
                    .newStatus(h.getNewStatus())
                    .changeDetail(h.getChangeDetail())
                    .changeDate(h.getChangeDate())
                    .userId(h.getUserId())
                    .userName(userName)
                    .build();
        }).collect(Collectors.toList());
    }
}
