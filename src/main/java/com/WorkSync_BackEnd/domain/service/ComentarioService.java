package com.WorkSync_BackEnd.domain.service;

import com.WorkSync_BackEnd.domain.dto.ComentarioDTO;
import com.WorkSync_BackEnd.domain.dto.ComentarioRequestDTO;
import com.WorkSync_BackEnd.exception.ResourceNotFoundException;
import com.WorkSync_BackEnd.persistence.entity.Comentario;
import com.WorkSync_BackEnd.persistence.entity.Tarea;
import com.WorkSync_BackEnd.persistence.entity.Usuario;
import com.WorkSync_BackEnd.persistence.mapper.ComentarioMapper;
import com.WorkSync_BackEnd.persistence.crud.ComentarioCrudRepository;
import com.WorkSync_BackEnd.persistence.crud.TareaCrudRepository;
import com.WorkSync_BackEnd.persistence.crud.UsuarioCrudRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComentarioService {

    private final ComentarioCrudRepository comentarioCrudRepository;
    private final TareaCrudRepository tareaCrudRepository;
    private final UsuarioCrudRepository usuarioCrudRepository;
    private final ComentarioMapper comentarioMapper;

    public ComentarioDTO createComentario(ComentarioRequestDTO dto) {
        Tarea tarea = tareaCrudRepository.findById(dto.getIdTarea())
                .orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada con ID: " + dto.getIdTarea()));

        Usuario usuario = usuarioCrudRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + dto.getIdUsuario()));

        Comentario comentario = comentarioMapper.toEntity(dto);
        comentario.setTarea(tarea);
        comentario.setUsuario(usuario);
        comentario.setFechaCreacion(LocalDateTime.now());

        Comentario savedComentario = comentarioCrudRepository.save(comentario);
        return comentarioMapper.toDto(savedComentario);
    }

    public List<ComentarioDTO> getComentariosByTarea(Long idTarea) {
        return comentarioCrudRepository.findByTarea_IdTareaOrderByFechaCreacionDesc(idTarea).stream()
                .map(comentarioMapper::toDto)
                .collect(Collectors.toList());
    }
}
