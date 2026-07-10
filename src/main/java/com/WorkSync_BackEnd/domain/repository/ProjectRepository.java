package com.WorkSync_BackEnd.domain.repository;

import com.WorkSync_BackEnd.domain.model.Project;
import java.util.List;
import java.util.Optional;

public interface ProjectRepository {
    List<Project> getAll();
    Optional<Project> getById(Long projectId);
    Project save(Project project);
}
