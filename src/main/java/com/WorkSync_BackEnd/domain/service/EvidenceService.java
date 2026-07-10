package com.WorkSync_BackEnd.domain.service;

import com.WorkSync_BackEnd.domain.dto.EvidenceDTO;
import com.WorkSync_BackEnd.domain.model.Evidence;
import com.WorkSync_BackEnd.domain.repository.EvidenceRepository;
import com.WorkSync_BackEnd.domain.repository.TaskRepository;
import com.WorkSync_BackEnd.domain.repository.UserRepository;
import com.WorkSync_BackEnd.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EvidenceService {

    private final EvidenceRepository evidenceRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final Cloudinary cloudinary;

    public EvidenceDTO uploadEvidence(Long taskId, Long userId, MultipartFile file) throws IOException {
        taskRepository.getById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada con ID: " + taskId));

        userRepository.getById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + userId));

        @SuppressWarnings("rawtypes")
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "resource_type", "auto",
                "folder", "worksync_evidencias"));

        String fileUrl = uploadResult.get("secure_url").toString();
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());

        Evidence evidenceDomain = Evidence.builder()
                .fileName(originalFilename)
                .mimeType(file.getContentType())
                .filePath(fileUrl)
                .uploadDate(LocalDateTime.now())
                .taskId(taskId)
                .userId(userId)
                .build();

        Evidence savedEvidence = evidenceRepository.save(evidenceDomain);
        return toDto(savedEvidence);
    }

    public List<EvidenceDTO> getEvidencesByTask(Long taskId) {
        return evidenceRepository.getByTaskId(taskId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private EvidenceDTO toDto(Evidence evidence) {
        return EvidenceDTO.builder()
                .evidenceId(evidence.getEvidenceId())
                .fileName(evidence.getFileName())
                .mimeType(evidence.getMimeType())
                .downloadUrl(evidence.getFilePath())
                .uploadDate(evidence.getUploadDate())
                .taskId(evidence.getTaskId())
                .userId(evidence.getUserId())
                .userName(evidence.getUserName())
                .build();
    }
}
