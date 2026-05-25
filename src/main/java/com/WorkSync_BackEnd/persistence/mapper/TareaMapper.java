package com.WorkSync_BackEnd.persistence.mapper;

import com.WorkSync_BackEnd.domain.dto.TareaRequestDTO;
import com.WorkSync_BackEnd.domain.dto.TareaResponseDTO;
import com.WorkSync_BackEnd.persistence.entity.Tarea;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TareaMapper {

    @Mapping(target = "proyecto.idProyecto", source = "idProyecto")
    Tarea toTarea(TareaRequestDTO requestDTO);

    @Mapping(target = "nombreUsuario", source = "responsable.nombre")
    TareaResponseDTO toResponseDTO(Tarea tarea);

    List<TareaResponseDTO> toResponseDTOs(List<Tarea> tareas);
}
