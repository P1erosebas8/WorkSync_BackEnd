package com.WorkSync_BackEnd.persistence.mapper;
import com.WorkSync_BackEnd.domain.model.TaskHistory;
import com.WorkSync_BackEnd.persistence.entity.HistorialTarea;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import java.util.List;
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TaskHistoryMapper {
    @Mapping(source = "idHistorial", target = "historyId")
    @Mapping(source = "idTarea", target = "taskId")
    @Mapping(source = "estadoAnterior", target = "oldStatus")
    @Mapping(source = "estadoNuevo", target = "newStatus")
    @Mapping(source = "detalleCambio", target = "changeDetail")
    @Mapping(source = "fechaCambio", target = "changeDate")
    @Mapping(source = "idUsuario", target = "userId")
    TaskHistory toTaskHistory(HistorialTarea historial);
    List<TaskHistory> toTaskHistories(List<HistorialTarea> historiales);

    @InheritInverseConfiguration
    HistorialTarea toHistorialTarea(TaskHistory taskHistory);
}
