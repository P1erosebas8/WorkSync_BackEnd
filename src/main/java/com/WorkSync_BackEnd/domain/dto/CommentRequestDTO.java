package com.WorkSync_BackEnd.domain.dto;

import lombok.Data;

@Data
public class CommentRequestDTO {
    private String content;
    private Long taskId;
    private Long userId;
}
