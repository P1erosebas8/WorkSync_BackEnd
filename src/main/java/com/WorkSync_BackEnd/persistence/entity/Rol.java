package com.WorkSync_BackEnd.persistence.entity;


import com.WorkSync_BackEnd.persistence.entity.enums.RolNombre;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rol")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRol;

    @Enumerated(EnumType.STRING)
    @Column(unique = true)
    private RolNombre nombre;

    private String descripcion;
}