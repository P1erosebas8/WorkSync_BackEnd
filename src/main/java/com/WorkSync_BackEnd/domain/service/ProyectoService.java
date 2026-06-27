package com.WorkSync_BackEnd.domain.service;

import com.WorkSync_BackEnd.persistence.crud.ProyectoCrudRepository;
import com.WorkSync_BackEnd.persistence.crud.AsignacionCrudRepository;
import com.WorkSync_BackEnd.persistence.crud.UsuarioCrudRepository;
import com.WorkSync_BackEnd.persistence.entity.Proyecto;
import com.WorkSync_BackEnd.persistence.entity.Asignacion;
import com.WorkSync_BackEnd.persistence.entity.Usuario;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProyectoService {
    private final ProyectoCrudRepository proyectoCrudRepository;
    private final AsignacionCrudRepository asignacionCrudRepository;
    private final UsuarioCrudRepository usuarioCrudRepository;

    public List<Proyecto> listar() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String emailUsuario = auth.getName();
        
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().contains("ADMIN"));
                
        List<Proyecto> proyectos;
        if (isAdmin) {
            proyectos = proyectoCrudRepository.findAll();
        } else {
            proyectos = asignacionCrudRepository.findProyectosByUsuarioEmail(emailUsuario);
        }
        
        LocalDate today = LocalDate.now();
        proyectos.forEach(p -> {
            if (p.getFechaLimite() != null && p.getFechaLimite().isBefore(today) && p.getEstado() != com.WorkSync_BackEnd.persistence.entity.enums.EstadoProyecto.ARCHIVADO) {
                p.setEstado(com.WorkSync_BackEnd.persistence.entity.enums.EstadoProyecto.ARCHIVADO);
                proyectoCrudRepository.save(p);
            }
        });
        
        return proyectos;
    }

    public Proyecto guardar(Proyecto proyecto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario admin = usuarioCrudRepository.findByCorreoElectronico(username)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));
                
        proyecto.setAdministrador(admin);
        proyecto.setEstado(com.WorkSync_BackEnd.persistence.entity.enums.EstadoProyecto.ACTIVO);
        return proyectoCrudRepository.save(proyecto);
    }

    public Proyecto editar(Long id, Proyecto proyectoDetalles) {
        return proyectoCrudRepository.findById(id).map(proyecto -> {
            proyecto.setNombre(proyectoDetalles.getNombre());
            proyecto.setDescripcion(proyectoDetalles.getDescripcion());
            proyecto.setFechaInicio(proyectoDetalles.getFechaInicio());
            proyecto.setFechaFin(proyectoDetalles.getFechaFin());
            proyecto.setFechaLimite(proyectoDetalles.getFechaLimite());
            return proyectoCrudRepository.save(proyecto);
        }).orElseThrow(() -> new RuntimeException("Proyecto no encontrado con id: " + id));
    }

    public Proyecto archivar(Long id) {
        return proyectoCrudRepository.findById(id).map(proyecto -> {
            if (proyecto.getEstado() == com.WorkSync_BackEnd.persistence.entity.enums.EstadoProyecto.ARCHIVADO) {
                proyecto.setEstado(com.WorkSync_BackEnd.persistence.entity.enums.EstadoProyecto.ACTIVO);
            } else {
                proyecto.setEstado(com.WorkSync_BackEnd.persistence.entity.enums.EstadoProyecto.ARCHIVADO);
            }
            return proyectoCrudRepository.save(proyecto);
        }).orElseThrow(() -> new RuntimeException("Proyecto no encontrado con id: " + id));
    }

    public Asignacion asignarColaborador(Long idProyecto, Long idUsuario) {
        Proyecto proyecto = proyectoCrudRepository.findById(idProyecto)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));
        Usuario usuario = usuarioCrudRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Asignacion asignacion = Asignacion.builder()
                .proyecto(proyecto)
                .usuario(usuario)
                .fechaAsignacion(LocalDate.now())
                .build();

        return asignacionCrudRepository.save(asignacion);
    }

    @Transactional
    public void removerColaborador(Long idProyecto, Long idUsuario) {
        asignacionCrudRepository.deleteByProyecto_IdProyectoAndUsuario_IdUsuario(idProyecto, idUsuario);
    }
}
