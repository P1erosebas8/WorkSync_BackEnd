package com.WorkSync_BackEnd.persistence.mapper;

import com.WorkSync_BackEnd.domain.model.Comment;
import com.WorkSync_BackEnd.persistence.entity.Comentario;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentMapper {
    @Mapping(source = "idComentario", target = "commentId")
    @Mapping(source = "contenido", target = "content")
    @Mapping(source = "fechaCreacion", target = "creationDate")
    @Mapping(source = "tarea.idTarea", target = "taskId")
    @Mapping(source = "usuario.idUsuario", target = "userId")
    @Mapping(source = "usuario.nombre", target = "userName")
    Comment toComment(Comentario comentario);

    List<Comment> toComments(List<Comentario> comentarios);

    @InheritInverseConfiguration
    @Mapping(target = "tarea", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    Comentario toComentarioEntity(Comment commentDomain);
}
