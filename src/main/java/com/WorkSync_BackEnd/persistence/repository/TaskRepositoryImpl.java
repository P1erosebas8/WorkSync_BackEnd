package com.WorkSync_BackEnd.persistence.repository;

import com.WorkSync_BackEnd.domain.model.Task;
import com.WorkSync_BackEnd.domain.repository.TaskRepository;
import com.WorkSync_BackEnd.persistence.crud.TareaCrudRepository;
import com.WorkSync_BackEnd.persistence.entity.Proyecto;
import com.WorkSync_BackEnd.persistence.entity.Tarea;
import com.WorkSync_BackEnd.persistence.entity.Usuario;
import com.WorkSync_BackEnd.persistence.mapper.TaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TaskRepositoryImpl implements TaskRepository {

    private final TareaCrudRepository tareaCrudRepository;
    private final TaskMapper taskMapper;

    @Override
    public List<Task> getAll() {
        return taskMapper.toTasks(tareaCrudRepository.findAll());
    }

    @Override
    public List<Task> getByProject(Long projectId) {
        return taskMapper.toTasks(tareaCrudRepository.findByProyecto_IdProyecto(projectId));
    }

    @Override
    public Optional<Task> getById(Long taskId) {
        return tareaCrudRepository.findById(taskId).map(taskMapper::toTask);
    }

    @Override
    public Task save(Task taskDomain) {
        Tarea tareaEntity = taskMapper.toTareaEntity(taskDomain);
        
        if (taskDomain.getProjectId() != null) {
            Proyecto proyecto = new Proyecto();
            proyecto.setIdProyecto(taskDomain.getProjectId());
            tareaEntity.setProyecto(proyecto);
        }
        
        if (taskDomain.getAssigneeId() != null) {
            Usuario responsable = new Usuario();
            responsable.setIdUsuario(taskDomain.getAssigneeId());
            tareaEntity.setResponsable(responsable);
        }
        
        return taskMapper.toTask(tareaCrudRepository.save(tareaEntity));
    }

    @Override
    public void delete(Long taskId) {
        tareaCrudRepository.deleteById(taskId);
    }
}
