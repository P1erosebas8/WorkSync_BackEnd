package com.WorkSync_BackEnd.persistence.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "historial_tarea")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HistorialTarea {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idHistorial;
    private Long idTarea;
    private String estadoAnterior;
    private String estadoNuevo;
    private String detalleCambio;
    private LocalDateTime fechaCambio;
    private Long idUsuario;
}
