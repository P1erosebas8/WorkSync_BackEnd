package com.WorkSync_BackEnd.persistence.mapper;

import com.WorkSync_BackEnd.domain.model.Evidence;
import com.WorkSync_BackEnd.persistence.entity.Evidencia;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EvidenceMapper {

    @Mapping(source = "idEvidencia", target = "evidenceId")
    @Mapping(source = "nombreArchivo", target = "fileName")
    @Mapping(source = "tipoMime", target = "mimeType")
    @Mapping(source = "rutaArchivo", target = "filePath")
    @Mapping(source = "fechaSubida", target = "uploadDate")
    @Mapping(source = "tarea.idTarea", target = "taskId")
    @Mapping(source = "usuario.idUsuario", target = "userId")
    @Mapping(source = "usuario.nombre", target = "userName")
    Evidence toEvidence(Evidencia evidencia);

    List<Evidence> toEvidences(List<Evidencia> evidencias);

    @InheritInverseConfiguration
    @Mapping(target = "tarea", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    Evidencia toEvidenciaEntity(Evidence evidenceDomain);
}
