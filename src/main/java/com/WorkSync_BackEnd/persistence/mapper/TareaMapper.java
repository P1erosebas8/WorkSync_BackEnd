package com.WorkSync_BackEnd.persistence.mapper;

import com.WorkSync_BackEnd.domain.dto.TareaRequestDTO;
import com.WorkSync_BackEnd.domain.dto.TareaResponseDTO;
import com.WorkSync_BackEnd.persistence.entity.Proyecto;
import com.WorkSync_BackEnd.persistence.entity.Tarea;
import com.WorkSync_BackEnd.persistence.entity.Usuario;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TareaMapper {

    public Tarea toTarea(TareaRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }

        Tarea.TareaBuilder tarea = Tarea.builder();

        tarea.titulo(requestDTO.titulo());
        tarea.descripcion(requestDTO.descripcion());
        tarea.prioridad(requestDTO.prioridad());

        if (requestDTO.idProyecto() != null) {
            Proyecto proyecto = new Proyecto();
            proyecto.setIdProyecto(requestDTO.idProyecto());
            tarea.proyecto(proyecto);
        }

        if (requestDTO.idResponsable() != null) {
            Usuario responsable = new Usuario();
            responsable.setIdUsuario(requestDTO.idResponsable());
            tarea.responsable(responsable);
        }

        return tarea.build();
    }

    public TareaResponseDTO toResponseDTO(Tarea tarea) {
        if (tarea == null) {
            return null;
        }

        return new TareaResponseDTO(
                tarea.getIdTarea(),
                tarea.getTitulo(),
                tarea.getDescripcion(),
                tarea.getPrioridad(),
                tarea.getEstado(),
                tarea.getResponsable() != null ? tarea.getResponsable().getNombre() : null,
                tarea.getResponsable() != null ? tarea.getResponsable().getIdUsuario() : null
        );
    }

    public List<TareaResponseDTO> toResponseDTOs(List<Tarea> tareas) {
        if (tareas == null) {
            return null;
        }

        return tareas.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
}
