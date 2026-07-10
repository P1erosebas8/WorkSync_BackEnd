package com.WorkSync_BackEnd.persistence.entity;

import com.WorkSync_BackEnd.persistence.entity.enums.EstadoTarea;
import com.WorkSync_BackEnd.persistence.entity.enums.Prioridad;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
@Entity
@Table(name = "tarea")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tarea {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long idTarea;
private String titulo;
private String descripcion;
@Enumerated(EnumType.STRING)
private EstadoTarea estado;
@Enumerated(EnumType.STRING)
private Prioridad prioridad;
private LocalDate fechaVencimiento;
private Integer porcentajeAvance;
@ManyToOne
@JoinColumn(name = "id_proyecto")
private Proyecto proyecto;
@ManyToOne
@JoinColumn(name = "id_usuario")
private Usuario responsable;
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "id_tarea_dependencia")
private Tarea dependeDe;
}
