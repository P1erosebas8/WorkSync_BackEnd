package com.WorkSync_BackEnd.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ComentarioDTO {
    private Long idComentario;
    private String contenido;
    private LocalDateTime fechaCreacion;
    private Long idTarea;
    private Long idUsuario;
    private String nombreUsuario;
}
