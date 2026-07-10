package com.WorkSync_BackEnd.persistence.crud;
import com.WorkSync_BackEnd.persistence.entity.HistorialTarea;
import org.springframework.data.repository.CrudRepository;
import java.util.List;
public interface HistorialTareaCrudRepository extends CrudRepository<HistorialTarea, Long> {
    List<HistorialTarea> findByIdTareaOrderByFechaCambioDesc(Long idTarea);
}
