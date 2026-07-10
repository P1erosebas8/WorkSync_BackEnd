package com.WorkSync_BackEnd.domain.dto;
import lombok.Builder;
import lombok.Data;
@Data @Builder
public class ProjectMetricsDTO {
    private int totalTasks;
    private int completedTasks;
    private int pendingTasks;
    private int inProgressTasks;
    private int overdueTasks;
    private int onTimeTasks;
    private double efficiencyPercentage;
}
