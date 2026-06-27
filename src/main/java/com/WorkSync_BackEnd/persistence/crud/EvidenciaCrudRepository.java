package com.WorkSync_BackEnd.persistence.crud;

import com.WorkSync_BackEnd.persistence.entity.Evidencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvidenciaCrudRepository extends JpaRepository<Evidencia, Long> {
    List<Evidencia> findByTarea_IdTareaOrderByFechaSubidaDesc(Long idTarea);
}
