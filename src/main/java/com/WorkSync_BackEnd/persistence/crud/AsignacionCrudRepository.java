package com.WorkSync_BackEnd.persistence.crud;

import com.WorkSync_BackEnd.persistence.entity.Asignacion;
import com.WorkSync_BackEnd.persistence.entity.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface AsignacionCrudRepository extends JpaRepository<Asignacion, Long> {
    void deleteByProyecto_IdProyectoAndUsuario_IdUsuario(Long idProyecto, Long idUsuario);
    List<Asignacion> findByProyecto_IdProyecto(Long idProyecto);
    
    @Query("SELECT a.proyecto FROM Asignacion a WHERE a.usuario.correoElectronico = :email")
    List<Proyecto> findProyectosByUsuarioEmail(@Param("email") String email);
}
