package com.WorkSync_BackEnd.persistence.mapper;

import com.WorkSync_BackEnd.domain.model.Assignment;
import com.WorkSync_BackEnd.persistence.entity.Asignacion;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {UserMapper.class, ProjectMapper.class})
public interface AssignmentMapper {

    @Mapping(source = "idAsignacion", target = "assignmentId")
    @Mapping(source = "usuario", target = "user")
    @Mapping(source = "proyecto", target = "project")
    @Mapping(source = "fechaAsignacion", target = "assignmentDate")
    Assignment toAssignment(Asignacion asignacion);

    List<Assignment> toAssignments(List<Asignacion> asignaciones);

    @InheritInverseConfiguration
    Asignacion toAsignacion(Assignment assignment);
}
