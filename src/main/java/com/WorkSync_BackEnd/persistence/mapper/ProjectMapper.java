package com.WorkSync_BackEnd.persistence.mapper;

import com.WorkSync_BackEnd.domain.model.Project;
import com.WorkSync_BackEnd.persistence.entity.Proyecto;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {UserMapper.class})
public interface ProjectMapper {

    @Mapping(source = "idProyecto", target = "projectId")
    @Mapping(source = "nombre", target = "name")
    @Mapping(source = "descripcion", target = "description")
    @Mapping(source = "fechaInicio", target = "startDate")
    @Mapping(source = "fechaFin", target = "endDate")
    @Mapping(source = "fechaLimite", target = "deadline")
    @Mapping(source = "estado", target = "status")
    @Mapping(source = "administrador", target = "administrator")
    Project toProject(Proyecto proyecto);

    List<Project> toProjects(List<Proyecto> proyectos);

    @InheritInverseConfiguration
    Proyecto toProyecto(Project project);
}
