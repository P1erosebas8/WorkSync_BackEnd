package com.WorkSync_BackEnd.domain.service;

import com.WorkSync_BackEnd.domain.dto.AsignacionRequestDTO;
import com.WorkSync_BackEnd.domain.model.Assignment;
import com.WorkSync_BackEnd.domain.model.Project;
import com.WorkSync_BackEnd.domain.model.User;
import com.WorkSync_BackEnd.domain.repository.AssignmentRepository;
import com.WorkSync_BackEnd.domain.repository.ProjectRepository;
import com.WorkSync_BackEnd.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AssignmentService {

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    public Assignment assignUserToProject(AsignacionRequestDTO dto) {
        User user = userRepository.getById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + dto.getUserId()));

        Project project = projectRepository.getById(dto.getProjectId())
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con ID: " + dto.getProjectId()));

        Assignment assignment = Assignment.builder()
                .user(user)
                .project(project)
                .assignmentDate(LocalDate.now())
                .build();

        return assignmentRepository.save(assignment);
    }
    
    public List<Assignment> getAssignmentsByProject(Long projectId) {
        return assignmentRepository.getAssignmentsByProject(projectId);
    }
}
