package com.WorkSync_BackEnd.persistence.mapper;

import com.WorkSync_BackEnd.domain.dto.ComentarioDTO;
import com.WorkSync_BackEnd.domain.dto.ComentarioRequestDTO;
import com.WorkSync_BackEnd.persistence.entity.Comentario;
import org.springframework.stereotype.Component;

@Component
public class ComentarioMapper {
    public ComentarioDTO toDto(Comentario comentario) {
        if (comentario == null) {
            return null;
        }

        return ComentarioDTO.builder()
                .idComentario(comentario.getIdComentario())
                .contenido(comentario.getContenido())
                .fechaCreacion(comentario.getFechaCreacion())
                .idTarea(comentario.getTarea() != null ? comentario.getTarea().getIdTarea() : null)
                .idUsuario(comentario.getUsuario() != null ? comentario.getUsuario().getIdUsuario() : null)
                .nombreUsuario(comentario.getUsuario() != null ? comentario.getUsuario().getNombre() : null)
                .build();
    }

    public Comentario toEntity(ComentarioRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Comentario comentario = new Comentario();
        comentario.setContenido(dto.getContenido());
        return comentario;
    }
}
