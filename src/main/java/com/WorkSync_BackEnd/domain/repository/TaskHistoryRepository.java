package com.WorkSync_BackEnd.domain.repository;
import com.WorkSync_BackEnd.domain.model.TaskHistory;
import java.util.List;
public interface TaskHistoryRepository {
    TaskHistory save(TaskHistory taskHistory);
    List<TaskHistory> getByTaskId(Long taskId);
}
