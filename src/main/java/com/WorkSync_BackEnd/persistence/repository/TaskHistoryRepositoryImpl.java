package com.WorkSync_BackEnd.persistence.repository;
import com.WorkSync_BackEnd.domain.model.TaskHistory;
import com.WorkSync_BackEnd.domain.repository.TaskHistoryRepository;
import com.WorkSync_BackEnd.persistence.crud.HistorialTareaCrudRepository;
import com.WorkSync_BackEnd.persistence.entity.HistorialTarea;
import com.WorkSync_BackEnd.persistence.mapper.TaskHistoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
@RequiredArgsConstructor
public class TaskHistoryRepositoryImpl implements TaskHistoryRepository {
    private final HistorialTareaCrudRepository crudRepository;
    private final TaskHistoryMapper mapper;

    @Override
    public TaskHistory save(TaskHistory taskHistory) {
        HistorialTarea historial = mapper.toHistorialTarea(taskHistory);
        return mapper.toTaskHistory(crudRepository.save(historial));
    }

    @Override
    public List<TaskHistory> getByTaskId(Long taskId) {
        return mapper.toTaskHistories(crudRepository.findByIdTareaOrderByFechaCambioDesc(taskId));
    }
}
