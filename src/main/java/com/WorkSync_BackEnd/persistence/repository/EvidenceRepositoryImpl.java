package com.WorkSync_BackEnd.persistence.repository;

import com.WorkSync_BackEnd.domain.repository.EvidenceRepository;
import com.WorkSync_BackEnd.persistence.crud.EvidenciaCrudRepository;
import com.WorkSync_BackEnd.persistence.entity.Evidencia;
import com.WorkSync_BackEnd.persistence.entity.Tarea;
import com.WorkSync_BackEnd.persistence.entity.Usuario;
import com.WorkSync_BackEnd.persistence.mapper.EvidenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EvidenceRepositoryImpl implements EvidenceRepository {
    private final EvidenciaCrudRepository evidenciaCrudRepository;
    private final EvidenceMapper evidenceMapper;

    @Override
    public List<com.WorkSync_BackEnd.domain.model.Evidence> getByTaskId(Long taskId) {
        return evidenceMapper.toEvidences(evidenciaCrudRepository.findByTarea_IdTareaOrderByFechaSubidaDesc(taskId));
    }

    @Override
    public Optional<com.WorkSync_BackEnd.domain.model.Evidence> getById(Long evidenceId) {
        return evidenciaCrudRepository.findById(evidenceId).map(evidenceMapper::toEvidence);
    }

    @Override
    public com.WorkSync_BackEnd.domain.model.Evidence save(com.WorkSync_BackEnd.domain.model.Evidence evidenceDomain) {
        Evidencia entity = evidenceMapper.toEvidenciaEntity(evidenceDomain);

        if (evidenceDomain.getTaskId() != null) {
            Tarea tarea = new Tarea();
            tarea.setIdTarea(evidenceDomain.getTaskId());
            entity.setTarea(tarea);
        }

        if (evidenceDomain.getUserId() != null) {
            Usuario usuario = new Usuario();
            usuario.setIdUsuario(evidenceDomain.getUserId());
            entity.setUsuario(usuario);
        }

        return evidenceMapper.toEvidence(evidenciaCrudRepository.save(entity));
    }
}
