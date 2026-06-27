package com.WorkSync_BackEnd.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ComentarioRequestDTO {
    private String contenido;
    private Long idTarea;
    private Long idUsuario;
}
