package com.WorkSync_BackEnd.domain.service;

import com.WorkSync_BackEnd.domain.dto.TareaRequestDTO;
import com.WorkSync_BackEnd.domain.dto.TareaResponseDTO;
import com.WorkSync_BackEnd.persistence.mapper.TareaMapper;
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
    private final TareaMapper tareaMapper;

    public List<TareaResponseDTO> listar() {
        return tareaMapper.toResponseDTOs(tareaCrudRepository.findAll());
    }

    public TareaResponseDTO guardar(TareaRequestDTO requestDTO) {
        if (requestDTO.idProyecto() == null) {
            throw new RuntimeException("La tarea debe estar asociada a un proyecto.");
        }
        Proyecto proyecto = proyectoCrudRepository.findById(requestDTO.idProyecto())
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));
        
        Tarea tarea = tareaMapper.toTarea(requestDTO);
        tarea.setProyecto(proyecto);
        
        if (tarea.getEstado() == null) {
            tarea.setEstado(EstadoTarea.PENDIENTE);
        }
        return tareaMapper.toResponseDTO(tareaCrudRepository.save(tarea));
    }

    public List<TareaResponseDTO> listarPorProyecto(Long idProyecto) {
        return tareaMapper.toResponseDTOs(tareaCrudRepository.findByProyecto_IdProyecto(idProyecto));
    }

    public TareaResponseDTO editar(Long id, TareaRequestDTO requestDTO) {
        Tarea tareaActualizada = tareaCrudRepository.findById(id).map(tarea -> {
            tarea.setTitulo(requestDTO.titulo());
            tarea.setDescripcion(requestDTO.descripcion());
            if (requestDTO.prioridad() != null) {
                tarea.setPrioridad(requestDTO.prioridad());
            }
            if (requestDTO.idProyecto() != null) {
                Proyecto proyecto = proyectoCrudRepository.findById(requestDTO.idProyecto())
                        .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));
                tarea.setProyecto(proyecto);
            }
            return tareaCrudRepository.save(tarea);
        }).orElseThrow(() -> new RuntimeException("Tarea no encontrada con id: " + id));
        return tareaMapper.toResponseDTO(tareaActualizada);
    }

    public TareaResponseDTO actualizarEstado(Long id, EstadoTarea nuevoEstado) {
        Tarea tareaActualizada = tareaCrudRepository.findById(id).map(tarea -> {
            tarea.setEstado(nuevoEstado);
            return tareaCrudRepository.save(tarea);
        }).orElseThrow(() -> new RuntimeException("Tarea no encontrada con id: " + id));
        return tareaMapper.toResponseDTO(tareaActualizada);
    }
}
