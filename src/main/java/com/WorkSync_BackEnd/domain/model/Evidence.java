package com.WorkSync_BackEnd.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Evidence {
    private Long evidenceId;
    private String fileName;
    private String mimeType;
    private String filePath;
    private LocalDateTime uploadDate;
    private Long taskId;
    private Long userId;
    private String userName;
}
