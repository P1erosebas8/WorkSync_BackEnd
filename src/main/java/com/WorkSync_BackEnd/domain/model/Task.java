package com.WorkSync_BackEnd.domain.model;

import com.WorkSync_BackEnd.persistence.entity.enums.EstadoTarea;
import com.WorkSync_BackEnd.persistence.entity.enums.Prioridad;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {
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
}
