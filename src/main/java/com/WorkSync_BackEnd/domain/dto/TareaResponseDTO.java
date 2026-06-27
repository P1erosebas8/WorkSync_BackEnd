package com.WorkSync_BackEnd.domain.dto;

import com.WorkSync_BackEnd.persistence.entity.enums.EstadoTarea;
import com.WorkSync_BackEnd.persistence.entity.enums.Prioridad;

public record TareaResponseDTO(
        Long idTarea,
        String titulo,
        String descripcion,
        Prioridad prioridad,
        EstadoTarea estado,
        String nombreUsuario,
        Long idResponsable
) {
}
