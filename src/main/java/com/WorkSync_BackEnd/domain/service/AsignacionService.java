package com.WorkSync_BackEnd.domain.service;

import com.WorkSync_BackEnd.domain.dto.AsignacionRequestDTO;
import com.WorkSync_BackEnd.persistence.crud.AsignacionCrudRepository;
import com.WorkSync_BackEnd.persistence.crud.ProyectoCrudRepository;
import com.WorkSync_BackEnd.persistence.crud.UsuarioCrudRepository;
import com.WorkSync_BackEnd.persistence.entity.Asignacion;
import com.WorkSync_BackEnd.persistence.entity.Proyecto;
import com.WorkSync_BackEnd.persistence.entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AsignacionService {

    @Autowired
    private AsignacionCrudRepository asignacionRepository;

    @Autowired
    private UsuarioCrudRepository usuarioRepository;

    @Autowired
    private ProyectoCrudRepository proyectoRepository;

    public Asignacion asignarUsuarioAProyecto(AsignacionRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + dto.getIdUsuario()));

        Proyecto proyecto = proyectoRepository.findById(dto.getIdProyecto())
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con ID: " + dto.getIdProyecto()));

        Asignacion asignacion = Asignacion.builder()
                .usuario(usuario)
                .proyecto(proyecto)
                .fechaAsignacion(LocalDate.now())
                .build();

        return asignacionRepository.save(asignacion);
    }
    
    public List<Asignacion> obtenerAsignacionesPorProyecto(Long idProyecto) {
        return asignacionRepository.findByProyecto_IdProyecto(idProyecto);
    }
}
