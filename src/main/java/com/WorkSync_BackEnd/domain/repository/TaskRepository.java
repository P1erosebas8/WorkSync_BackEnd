package com.WorkSync_BackEnd.domain.repository;

import com.WorkSync_BackEnd.domain.model.Task;
import java.util.List;
import java.util.Optional;

public interface TaskRepository {
    List<Task> getAll();
    List<Task> getByProject(Long projectId);
    Optional<Task> getById(Long taskId);
    Task save(Task task);
    void delete(Long taskId);
}
