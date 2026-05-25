package com.WorkSync_BackEnd.persistence.entity;

import com.WorkSync_BackEnd.persistence.entity.enums.EstadoProyecto;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
@Entity
@Table(name = "proyecto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Proyecto {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long idProyecto;
private String nombre;
private String descripcion;
private LocalDate fechaInicio;
private LocalDate fechaFin;
@Enumerated(EnumType.STRING)
private EstadoProyecto estado;
@ManyToOne
@JoinColumn(name = "id_usuario_admin")
private Usuario administrador;
}
