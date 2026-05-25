package com.WorkSync_BackEnd.domain.service;

import com.WorkSync_BackEnd.persistence.crud.TareaCrudRepository;
import com.WorkSync_BackEnd.persistence.crud.ProyectoCrudRepository;
import com.WorkSync_BackEnd.persistence.entity.Tarea;
import com.WorkSync_BackEnd.persistence.entity.Proyecto;
import com.WorkSync_BackEnd.persistence.entity.enums.EstadoTarea;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TareaService {
    private final TareaCrudRepository tareaCrudRepository;
    private final ProyectoCrudRepository proyectoCrudRepository;

    public List<Tarea> listar() {
        return tareaCrudRepository.findAll();
    }

    public Tarea guardar(Tarea tarea) {
        if (tarea.getProyecto() == null || tarea.getProyecto().getIdProyecto() == null) {
            throw new RuntimeException("La tarea debe estar asociada a un proyecto.");
        }
        Proyecto proyecto = proyectoCrudRepository.findById(tarea.getProyecto().getIdProyecto())
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));
        
        tarea.setProyecto(proyecto);
        
        if (tarea.getEstado() == null) {
            tarea.setEstado(EstadoTarea.PENDIENTE);
        }
        return tareaCrudRepository.save(tarea);
    }

    public List<Tarea> listarPorProyecto(Long idProyecto) {
        return tareaCrudRepository.findByProyecto_IdProyecto(idProyecto);
    }

    public Tarea editar(Long id, Tarea tareaDetalles) {
        return tareaCrudRepository.findById(id).map(tarea -> {
            tarea.setTitulo(tareaDetalles.getTitulo());
            tarea.setDescripcion(tareaDetalles.getDescripcion());
            if (tareaDetalles.getPrioridad() != null) {
                tarea.setPrioridad(tareaDetalles.getPrioridad());
            }
            return tareaCrudRepository.save(tarea);
        }).orElseThrow(() -> new RuntimeException("Tarea no encontrada con id: " + id));
    }

    public Tarea actualizarEstado(Long id, EstadoTarea nuevoEstado) {
        return tareaCrudRepository.findById(id).map(tarea -> {
            tarea.setEstado(nuevoEstado);
            return tareaCrudRepository.save(tarea);
        }).orElseThrow(() -> new RuntimeException("Tarea no encontrada con id: " + id));
    }
}
