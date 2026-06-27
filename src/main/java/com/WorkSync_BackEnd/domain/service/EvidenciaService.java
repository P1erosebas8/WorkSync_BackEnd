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

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EvidenciaService {

    private final EvidenciaCrudRepository evidenciaCrudRepository;
    private final TareaCrudRepository tareaCrudRepository;
    private final UsuarioCrudRepository usuarioCrudRepository;
    private final EvidenciaMapper evidenciaMapper;

    // Inyectado automáticamente por @RequiredArgsConstructor gracias a CloudinaryConfig
    private final Cloudinary cloudinary;

    public EvidenciaDTO uploadEvidencia(Long idTarea, Long idUsuario, MultipartFile file) throws IOException {
        Tarea tarea = tareaCrudRepository.findById(idTarea)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada con ID: " + idTarea));

        Usuario usuario = usuarioCrudRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + idUsuario));

        // Subir a Cloudinary (resource_type "auto" permite PDFs, Word, Zip, Imágenes, etc.)
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "resource_type", "auto",
                "folder", "worksync_evidencias"
        ));

        String fileUrl = uploadResult.get("secure_url").toString();
        String publicId = uploadResult.get("public_id").toString();
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());

        // Crear entidad
        Evidencia evidencia = Evidencia.builder()
                .nombreArchivo(originalFilename)
                .tipoMime(file.getContentType())
                .rutaArchivo(fileUrl) // Guardamos la URL pública
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

    // Ya no se usa Resource local, el frontend usará directamente la URL guardada en rutaArchivo
    public Evidencia getEvidenciaEntity(Long idEvidencia) {
        return evidenciaCrudRepository.findById(idEvidencia)
                .orElseThrow(() -> new ResourceNotFoundException("Evidencia no encontrada"));
    }
}
