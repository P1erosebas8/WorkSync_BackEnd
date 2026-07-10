package com.WorkSync_BackEnd.domain.repository;

import com.WorkSync_BackEnd.domain.model.Evidence;
import java.util.List;
import java.util.Optional;

public interface EvidenceRepository {
    List<Evidence> getByTaskId(Long taskId);
    Optional<Evidence> getById(Long evidenceId);
    Evidence save(Evidence evidence);
}
