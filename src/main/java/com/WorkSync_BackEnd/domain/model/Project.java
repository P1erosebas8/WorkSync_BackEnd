package com.WorkSync_BackEnd.domain.model;

import com.WorkSync_BackEnd.persistence.entity.enums.EstadoProyecto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Project {
    private Long projectId;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate deadline;
    private EstadoProyecto status;
    private User administrator;
}
