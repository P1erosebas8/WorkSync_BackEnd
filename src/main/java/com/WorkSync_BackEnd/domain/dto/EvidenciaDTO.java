package com.WorkSync_BackEnd.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EvidenciaDTO {
    private Long idEvidencia;
    private String nombreArchivo;
    private String tipoMime;
    private String urlDescarga;
    private LocalDateTime fechaSubida;
    private Long idTarea;
    private Long idUsuario;
    private String nombreUsuario;
}
