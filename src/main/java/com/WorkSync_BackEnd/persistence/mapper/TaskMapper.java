package com.WorkSync_BackEnd.persistence.mapper;

import com.WorkSync_BackEnd.domain.model.Task;
import com.WorkSync_BackEnd.persistence.entity.Tarea;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TaskMapper {

    @Mapping(source = "idTarea", target = "taskId")
    @Mapping(source = "titulo", target = "title")
    @Mapping(source = "descripcion", target = "description")
    @Mapping(source = "estado", target = "status")
    @Mapping(source = "prioridad", target = "priority")
    @Mapping(source = "fechaVencimiento", target = "dueDate")
    @Mapping(source = "porcentajeAvance", target = "progressPercentage")
    @Mapping(source = "proyecto.idProyecto", target = "projectId")
    @Mapping(source = "responsable.idUsuario", target = "assigneeId")
    @Mapping(source = "responsable.nombre", target = "assigneeName")
    @Mapping(source = "dependeDe.idTarea", target = "dependsOnTaskId")
    Task toTask(Tarea tarea);

    List<Task> toTasks(List<Tarea> tareas);

    @InheritInverseConfiguration
    @Mapping(target = "proyecto", ignore = true)
    @Mapping(target = "responsable", ignore = true)
    @Mapping(target = "dependeDe", ignore = true)
    Tarea toTareaEntity(Task taskDomain);
}
