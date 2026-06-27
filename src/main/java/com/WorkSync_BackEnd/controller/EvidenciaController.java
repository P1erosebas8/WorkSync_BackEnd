package com.WorkSync_BackEnd.controller;

import com.WorkSync_BackEnd.domain.dto.EvidenciaDTO;
import com.WorkSync_BackEnd.domain.service.EvidenciaService;
import com.WorkSync_BackEnd.persistence.entity.Evidencia;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
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
public class EvidenciaController {

    private final EvidenciaService evidenciaService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EvidenciaDTO> uploadEvidencia(
            @RequestParam("idTarea") Long idTarea,
            @RequestParam("idUsuario") Long idUsuario,
            @RequestParam("file") MultipartFile file) {
        try {
            EvidenciaDTO dto = evidenciaService.uploadEvidencia(idTarea, idUsuario, file);
            return new ResponseEntity<>(dto, HttpStatus.CREATED);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/tarea/{idTarea}")
    public ResponseEntity<List<EvidenciaDTO>> getEvidenciasByTarea(@PathVariable Long idTarea) {
        return ResponseEntity.ok(evidenciaService.getEvidenciasByTarea(idTarea));
    }

}
