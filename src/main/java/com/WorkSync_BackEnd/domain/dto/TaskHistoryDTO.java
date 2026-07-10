package com.WorkSync_BackEnd.domain.dto;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
@Data @Builder
public class TaskHistoryDTO {
    private Long historyId;
    private Long taskId;
    private String oldStatus;
    private String newStatus;
    private String changeDetail;
    private LocalDateTime changeDate;
    private Long userId;
    private String userName;
}
