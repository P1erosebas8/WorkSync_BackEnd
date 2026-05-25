package com.WorkSync_BackEnd.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    private String nombre;

    @Column(unique = true)
    private String correoElectronico;

    private String contrasena;

    private Boolean estado;

    @ManyToOne
    @JoinColumn(name = "id_rol")
    private Rol rol;
}