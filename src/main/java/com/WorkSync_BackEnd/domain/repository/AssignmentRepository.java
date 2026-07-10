package com.WorkSync_BackEnd.domain.repository;

import com.WorkSync_BackEnd.domain.model.Assignment;
import com.WorkSync_BackEnd.domain.model.Project;
import java.util.List;

public interface AssignmentRepository {
    List<Project> getProjectsByUserEmail(String email);
    List<Assignment> getAssignmentsByProject(Long projectId);
    Assignment save(Assignment assignment);
    void deleteByProjectAndUser(Long projectId, Long userId);
}
