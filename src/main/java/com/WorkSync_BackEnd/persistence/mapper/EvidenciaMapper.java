package com.WorkSync_BackEnd.persistence.mapper;

import com.WorkSync_BackEnd.domain.dto.EvidenciaDTO;
import com.WorkSync_BackEnd.persistence.entity.Evidencia;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Component
public class EvidenciaMapper {

    public EvidenciaDTO toDto(Evidencia evidencia) {
        if (evidencia == null) {
            return null;
        }

        String urlDescarga = evidencia.getRutaArchivo();

        return EvidenciaDTO.builder()
                .idEvidencia(evidencia.getIdEvidencia())
                .nombreArchivo(evidencia.getNombreArchivo())
                .tipoMime(evidencia.getTipoMime())
                .urlDescarga(urlDescarga)
                .fechaSubida(evidencia.getFechaSubida())
                .idTarea(evidencia.getTarea() != null ? evidencia.getTarea().getIdTarea() : null)
                .idUsuario(evidencia.getUsuario() != null ? evidencia.getUsuario().getIdUsuario() : null)
                .nombreUsuario(evidencia.getUsuario() != null ? evidencia.getUsuario().getNombre() : null)
                .build();
    }
}
