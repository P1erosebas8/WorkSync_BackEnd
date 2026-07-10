package com.WorkSync_BackEnd.domain.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class CommentDTO {
    private Long commentId;
    private String content;
    private LocalDateTime creationDate;
    private Long taskId;
    private Long userId;
    private String userName;
}
