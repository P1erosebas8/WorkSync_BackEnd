package com.WorkSync_BackEnd.persistence.repository;

import com.WorkSync_BackEnd.domain.repository.CommentRepository;
import com.WorkSync_BackEnd.persistence.crud.ComentarioCrudRepository;
import com.WorkSync_BackEnd.persistence.entity.Comentario;
import com.WorkSync_BackEnd.persistence.entity.Tarea;
import com.WorkSync_BackEnd.persistence.entity.Usuario;
import com.WorkSync_BackEnd.persistence.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepository {
    private final ComentarioCrudRepository comentarioCrudRepository;
    private final CommentMapper commentMapper;

    @Override
    public List<com.WorkSync_BackEnd.domain.model.Comment> getByTaskId(Long taskId) {
        return commentMapper.toComments(comentarioCrudRepository.findByTarea_IdTareaOrderByFechaCreacionDesc(taskId));
    }

    @Override
    public com.WorkSync_BackEnd.domain.model.Comment save(com.WorkSync_BackEnd.domain.model.Comment commentDomain) {
        Comentario entity = commentMapper.toComentarioEntity(commentDomain);
        
        if (commentDomain.getTaskId() != null) {
            Tarea tarea = new Tarea();
            tarea.setIdTarea(commentDomain.getTaskId());
            entity.setTarea(tarea);
        }
        
        if (commentDomain.getUserId() != null) {
            Usuario usuario = new Usuario();
            usuario.setIdUsuario(commentDomain.getUserId());
            entity.setUsuario(usuario);
        }
        
        return commentMapper.toComment(comentarioCrudRepository.save(entity));
    }
}
