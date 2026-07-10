package com.WorkSync_BackEnd.domain.dto;

import com.WorkSync_BackEnd.persistence.entity.enums.EstadoTarea;
import com.WorkSync_BackEnd.persistence.entity.enums.Prioridad;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class TaskResponseDTO {
    private Long taskId;
    private String title;
    private String description;
    private EstadoTarea status;
    private Prioridad priority;
    private LocalDate dueDate;
    private Integer progressPercentage;
    private Long projectId;
    private Long assigneeId;
    private String assigneeName;
    private Long dependsOnTaskId;
}
