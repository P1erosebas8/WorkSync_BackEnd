package com.WorkSync_BackEnd.persistence.repository;

import com.WorkSync_BackEnd.domain.model.Assignment;
import com.WorkSync_BackEnd.domain.model.Project;
import com.WorkSync_BackEnd.domain.repository.AssignmentRepository;
import com.WorkSync_BackEnd.persistence.crud.AsignacionCrudRepository;
import com.WorkSync_BackEnd.persistence.mapper.AssignmentMapper;
import com.WorkSync_BackEnd.persistence.mapper.ProjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AssignmentRepositoryImpl implements AssignmentRepository {
    private final AsignacionCrudRepository asignacionCrudRepository;
    private final AssignmentMapper assignmentMapper;
    private final ProjectMapper projectMapper;

    @Override
    public List<Project> getProjectsByUserEmail(String email) {
        return projectMapper.toProjects(asignacionCrudRepository.findProyectosByUsuarioEmail(email));
    }

    @Override
    public List<Assignment> getAssignmentsByProject(Long projectId) {
        return assignmentMapper.toAssignments(asignacionCrudRepository.findByProyecto_IdProyecto(projectId));
    }

    @Override
    public Assignment save(Assignment assignment) {
        return assignmentMapper.toAssignment(
                asignacionCrudRepository.save(assignmentMapper.toAsignacion(assignment))
        );
    }

    @Override
    public void deleteByProjectAndUser(Long projectId, Long userId) {
        asignacionCrudRepository.deleteByProyecto_IdProyectoAndUsuario_IdUsuario(projectId, userId);
    }
}
