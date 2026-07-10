package com.WorkSync_BackEnd.domain.model;
import lombok.*;
import java.time.LocalDateTime;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TaskHistory {
    private Long historyId;
    private Long taskId;
    private String oldStatus;
    private String newStatus;
    private String changeDetail;
    private LocalDateTime changeDate;
    private Long userId;
}
