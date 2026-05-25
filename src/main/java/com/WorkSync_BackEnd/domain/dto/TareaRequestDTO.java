package com.WorkSync_BackEnd.domain.dto;

import com.WorkSync_BackEnd.persistence.entity.enums.Prioridad;

public record TareaRequestDTO(
        String titulo,
        String descripcion,
        Prioridad prioridad,
        Long idProyecto
) {
}
