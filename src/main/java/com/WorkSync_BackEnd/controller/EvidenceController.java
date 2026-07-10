package com.WorkSync_BackEnd.controller;

import com.WorkSync_BackEnd.domain.dto.EvidenceDTO;
import com.WorkSync_BackEnd.domain.service.EvidenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/evidencias")
@RequiredArgsConstructor
public class EvidenceController {

    private final EvidenceService evidenceService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EvidenceDTO> uploadEvidence(
            @RequestParam("taskId") Long taskId,
            @RequestParam("userId") Long userId,
            @RequestParam("file") MultipartFile file) {
        try {
            EvidenceDTO dto = evidenceService.uploadEvidence(taskId, userId, file);
            return new ResponseEntity<>(dto, HttpStatus.CREATED);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/tarea/{taskId}")
    public ResponseEntity<List<EvidenceDTO>> getEvidencesByTask(@PathVariable Long taskId) {
        return ResponseEntity.ok(evidenceService.getEvidencesByTask(taskId));
    }
}
