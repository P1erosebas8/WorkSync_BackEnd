package com.WorkSync_BackEnd.persistence.crud;

import com.WorkSync_BackEnd.persistence.entity.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TareaCrudRepository extends JpaRepository<Tarea, Long> {
    List<Tarea> findByProyecto_IdProyecto(Long idProyecto);
}
