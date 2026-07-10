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
}
