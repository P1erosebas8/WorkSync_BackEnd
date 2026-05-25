package com.WorkSync_BackEnd.persistence.crud;

import com.WorkSync_BackEnd.persistence.entity.Rol;
import com.WorkSync_BackEnd.persistence.entity.enums.RolNombre;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RolCrudRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNombre(RolNombre nombre);
}
