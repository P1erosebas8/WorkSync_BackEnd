package com.WorkSync_BackEnd.persistence.crud;

import com.WorkSync_BackEnd.persistence.entity.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProyectoCrudRepository extends JpaRepository<Proyecto, Long> {
}
