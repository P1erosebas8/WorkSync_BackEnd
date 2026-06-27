package com.WorkSync_BackEnd.domain.service;

import com.WorkSync_BackEnd.domain.dto.EvidenciaDTO;
import com.WorkSync_BackEnd.exception.ResourceNotFoundException;
import com.WorkSync_BackEnd.persistence.crud.EvidenciaCrudRepository;
import com.WorkSync_BackEnd.persistence.crud.TareaCrudRepository;
import com.WorkSync_BackEnd.persistence.crud.UsuarioCrudRepository;
import com.WorkSync_BackEnd.persistence.entity.Evidencia;
import com.WorkSync_BackEnd.persistence.entity.Tarea;
import com.WorkSync_BackEnd.persistence.entity.Usuario;
import com.WorkSync_BackEnd.persistence.mapper.EvidenciaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EvidenciaService {

    private final EvidenciaCrudRepository evidenciaCrudRepository;
    private final TareaCrudRepository tareaCrudRepository;
    private final UsuarioCrudRepository usuarioCrudRepository;
    private final EvidenciaMapper evidenciaMapper;

    // Directorio donde se guardarán las evidencias (relativo al proyecto)
    private final String uploadDir = "uploads/evidencias/";

    public EvidenciaDTO uploadEvidencia(Long idTarea, Long idUsuario, MultipartFile file) throws IOException {
        Tarea tarea = tareaCrudRepository.findById(idTarea)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada con ID: " + idTarea));

        Usuario usuario = usuarioCrudRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + idUsuario));

        // Crear directorio si no existe
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Limpiar nombre y asegurar unicidad
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;

        // Guardar archivo en disco
        Path filePath = uploadPath.resolve(uniqueFileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Crear entidad
        Evidencia evidencia = Evidencia.builder()
                .nombreArchivo(fileName)
                .tipoMime(file.getContentType())
                .rutaArchivo(filePath.toString())
                .fechaSubida(LocalDateTime.now())
                .tarea(tarea)
                .usuario(usuario)
                .build();

        Evidencia savedEvidencia = evidenciaCrudRepository.save(evidencia);
        return evidenciaMapper.toDto(savedEvidencia);
    }

    public List<EvidenciaDTO> getEvidenciasByTarea(Long idTarea) {
        return evidenciaCrudRepository.findByTarea_IdTareaOrderByFechaSubidaDesc(idTarea).stream()
                .map(evidenciaMapper::toDto)
                .collect(Collectors.toList());
    }

    public Resource loadEvidenciaAsResource(Long idEvidencia) {
        Evidencia evidencia = evidenciaCrudRepository.findById(idEvidencia)
                .orElseThrow(() -> new ResourceNotFoundException("Evidencia no encontrada"));

        try {
            Path filePath = Paths.get(evidencia.getRutaArchivo()).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("Archivo no encontrado: " + evidencia.getNombreArchivo());
            }
        } catch (MalformedURLException ex) {
            throw new ResourceNotFoundException("Archivo no encontrado: " + evidencia.getNombreArchivo());
        }
    }

    public Evidencia getEvidenciaEntity(Long idEvidencia) {
        return evidenciaCrudRepository.findById(idEvidencia)
                .orElseThrow(() -> new ResourceNotFoundException("Evidencia no encontrada"));
    }
}
