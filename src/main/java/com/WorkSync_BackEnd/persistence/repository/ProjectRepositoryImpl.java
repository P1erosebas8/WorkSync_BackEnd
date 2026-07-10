package com.WorkSync_BackEnd.persistence.repository;

import com.WorkSync_BackEnd.domain.model.Project;
import com.WorkSync_BackEnd.domain.repository.ProjectRepository;
import com.WorkSync_BackEnd.persistence.crud.ProyectoCrudRepository;
import com.WorkSync_BackEnd.persistence.mapper.ProjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProjectRepositoryImpl implements ProjectRepository {
    private final ProyectoCrudRepository proyectoCrudRepository;
    private final ProjectMapper projectMapper;

    @Override
    public List<Project> getAll() {
        return projectMapper.toProjects(proyectoCrudRepository.findAll());
    }

    @Override
    public Optional<Project> getById(Long projectId) {
        return proyectoCrudRepository.findById(projectId).map(projectMapper::toProject);
    }

    @Override
    public Project save(Project project) {
        return projectMapper.toProject(
                proyectoCrudRepository.save(projectMapper.toProyecto(project))
        );
    }
}
