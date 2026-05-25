package com.WorkSync_BackEnd.persistence.crud;

import com.WorkSync_BackEnd.persistence.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface UsuarioCrudRepository extends JpaRepository<Usuario, Long> {
Optional<Usuario> findByCorreoElectronico(String correoElectronico);
}
