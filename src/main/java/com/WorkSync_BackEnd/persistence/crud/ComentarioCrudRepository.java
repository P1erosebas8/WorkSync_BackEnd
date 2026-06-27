package com.WorkSync_BackEnd.persistence.crud;

import com.WorkSync_BackEnd.persistence.entity.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComentarioCrudRepository extends JpaRepository<Comentario, Long> {
    List<Comentario> findByTarea_IdTareaOrderByFechaCreacionDesc(Long idTarea);
}
