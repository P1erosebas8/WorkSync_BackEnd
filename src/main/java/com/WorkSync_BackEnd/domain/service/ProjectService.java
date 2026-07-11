package com.WorkSync_BackEnd.domain.service;

import com.WorkSync_BackEnd.domain.model.Assignment;
import com.WorkSync_BackEnd.domain.model.Project;
import com.WorkSync_BackEnd.domain.model.User;
import com.WorkSync_BackEnd.domain.repository.AssignmentRepository;
import com.WorkSync_BackEnd.domain.repository.ProjectRepository;
import com.WorkSync_BackEnd.domain.repository.UserRepository;
import com.WorkSync_BackEnd.persistence.entity.enums.EstadoProyecto;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;
    private final com.WorkSync_BackEnd.domain.repository.TaskRepository taskRepository;

    public List<Project> getAll() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().contains("ADMIN"));
                
        List<Project> projects;
        if (isAdmin) {
            projects = projectRepository.getAll();
        } else {
            projects = assignmentRepository.getProjectsByUserEmail(userEmail);
        }
        
        LocalDate today = LocalDate.now();
        projects.forEach(p -> {
            if (p.getDeadline() != null && p.getDeadline().isBefore(today) && p.getStatus() != EstadoProyecto.ARCHIVADO) {
                p.setStatus(EstadoProyecto.ARCHIVADO);
                projectRepository.save(p);
            }
        });
        
        return projects;
    }

    public Project save(Project project) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User admin = userRepository.getByEmail(username)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));
                
        project.setAdministrator(admin);
        project.setStatus(EstadoProyecto.ACTIVO);
        if (project.getStartDate() == null) {
            project.setStartDate(LocalDate.now());
        }
        return projectRepository.save(project);
    }

    public Project update(Long id, Project projectDetails) {
        return projectRepository.getById(id).map(project -> {
            project.setName(projectDetails.getName());
            project.setDescription(projectDetails.getDescription());
            project.setStartDate(projectDetails.getStartDate());
            project.setEndDate(projectDetails.getEndDate());
            project.setDeadline(projectDetails.getDeadline());
            return projectRepository.save(project);
        }).orElseThrow(() -> new RuntimeException("Proyecto no encontrado con id: " + id));
    }

    public Project archive(Long id) {
        return projectRepository.getById(id).map(project -> {
            if (project.getStatus() == EstadoProyecto.ARCHIVADO) {
                project.setStatus(EstadoProyecto.ACTIVO);
            } else {
                project.setStatus(EstadoProyecto.ARCHIVADO);
            }
            return projectRepository.save(project);
        }).orElseThrow(() -> new RuntimeException("Proyecto no encontrado con id: " + id));
    }

    public Assignment assignCollaborator(Long projectId, Long userId) {
        Project project = projectRepository.getById(projectId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));
        User user = userRepository.getById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Assignment assignment = Assignment.builder()
                .project(project)
                .user(user)
                .assignmentDate(LocalDate.now())
                .build();

        return assignmentRepository.save(assignment);
    }

    @Transactional
    public void removeCollaborator(Long projectId, Long userId) {
        assignmentRepository.deleteByProjectAndUser(projectId, userId);
    }

    public com.WorkSync_BackEnd.domain.dto.ProjectMetricsDTO getMetrics(Long projectId) {
        List<com.WorkSync_BackEnd.domain.model.Task> tasks = taskRepository.getByProject(projectId);
        int total = tasks.size();
        if (total == 0) {
            return com.WorkSync_BackEnd.domain.dto.ProjectMetricsDTO.builder().build();
        }

        int completed = 0;
        int pending = 0;
        int inProgress = 0;
        int overdue = 0;
        int onTime = 0;
        int blocked = 0;

        java.time.LocalDate today = java.time.LocalDate.now();

        for (com.WorkSync_BackEnd.domain.model.Task task : tasks) {
            switch (task.getStatus()) {
                case COMPLETADO: completed++; break;
                case PENDIENTE: pending++; break;
                case EN_PROGRESO:
                case EN_REVISION:
                    inProgress++; break;
                case BLOQUEADO:
                    blocked++; break;
            }
            
            if (task.getDueDate() != null) {
                if (task.getStatus() != com.WorkSync_BackEnd.persistence.entity.enums.EstadoTarea.COMPLETADO 
                    && task.getDueDate().isBefore(today)) {
                    overdue++;
                } else if (task.getStatus() == com.WorkSync_BackEnd.persistence.entity.enums.EstadoTarea.COMPLETADO) {
                    onTime++; // simplified logic for on time
                }
            }
        }

        double efficiency = ((double) completed / total) * 100.0;
        efficiency = Math.round(efficiency * 100.0) / 100.0;

        return com.WorkSync_BackEnd.domain.dto.ProjectMetricsDTO.builder()
                .totalTasks(total)
                .completedTasks(completed)
                .pendingTasks(pending)
                .inProgressTasks(inProgress)
                .blockedTasks(blocked)
                .overdueTasks(overdue)
                .onTimeTasks(onTime)
                .efficiencyPercentage(efficiency)
                .build();
    }

    public com.WorkSync_BackEnd.domain.dto.GlobalMetricsDTO getGlobalMetrics() {
        List<Project> projects = projectRepository.getAll();
        int totalProjects = projects.size();
        int activeProjects = 0;
        int archivedProjects = 0;
        
        int totalTasks = 0;
        int completedTasks = 0;
        int pendingTasks = 0;

        for (Project p : projects) {
            if (p.getStatus() == EstadoProyecto.ARCHIVADO) {
                archivedProjects++;
            } else {
                activeProjects++;
            }
            
            List<com.WorkSync_BackEnd.domain.model.Task> tasks = taskRepository.getByProject(p.getProjectId());
            for (com.WorkSync_BackEnd.domain.model.Task t : tasks) {
                totalTasks++;
                if (t.getStatus() == com.WorkSync_BackEnd.persistence.entity.enums.EstadoTarea.COMPLETADO) {
                    completedTasks++;
                } else if (t.getStatus() == com.WorkSync_BackEnd.persistence.entity.enums.EstadoTarea.PENDIENTE) {
                    pendingTasks++;
                }
            }
        }

        double globalEfficiency = totalTasks > 0 ? ((double) completedTasks / totalTasks) * 100.0 : 0.0;
        globalEfficiency = Math.round(globalEfficiency * 100.0) / 100.0;

        return com.WorkSync_BackEnd.domain.dto.GlobalMetricsDTO.builder()
                .totalProjects(totalProjects)
                .activeProjects(activeProjects)
                .archivedProjects(archivedProjects)
                .totalTasks(totalTasks)
                .completedTasks(completedTasks)
                .pendingTasks(pendingTasks)
                .globalEfficiency(globalEfficiency)
                .build();
    }
}
