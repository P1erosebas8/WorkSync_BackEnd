package com.WorkSync_BackEnd.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Assignment {
    private Long assignmentId;
    private User user;
    private Project project;
    private LocalDate assignmentDate;
}
