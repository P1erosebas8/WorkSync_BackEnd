package com.WorkSync_BackEnd.domain.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class EvidenceDTO {
    private Long evidenceId;
    private String fileName;
    private String mimeType;
    private String downloadUrl;
    private LocalDateTime uploadDate;
    private Long taskId;
    private Long userId;
    private String userName;
}
