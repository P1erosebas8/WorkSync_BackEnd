package com.WorkSync_BackEnd.domain.dto;
import lombok.Builder;
import lombok.Data;
@Data @Builder
public class GlobalMetricsDTO {
    private int totalProjects;
    private int activeProjects;
    private int archivedProjects;
    private int totalTasks;
    private int completedTasks;
    private int pendingTasks;
    private double globalEfficiency;
}
