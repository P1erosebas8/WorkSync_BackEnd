package com.WorkSync_BackEnd.config;

import com.WorkSync_BackEnd.persistence.crud.RolCrudRepository;
import com.WorkSync_BackEnd.persistence.crud.UsuarioCrudRepository;
import com.WorkSync_BackEnd.persistence.entity.Rol;
import com.WorkSync_BackEnd.persistence.entity.Usuario;
import com.WorkSync_BackEnd.persistence.entity.enums.RolNombre;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioCrudRepository usuarioCrudRepository;
    private final RolCrudRepository rolCrudRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (usuarioCrudRepository.findByCorreoElectronico("admin@worksync.com").isEmpty()) {
            
            Rol adminRol = rolCrudRepository.findByNombre(RolNombre.ADMIN)
                    .orElseGet(() -> rolCrudRepository.save(Rol.builder()
                            .nombre(RolNombre.ADMIN)
                            .descripcion("Rol de Administrador")
                            .build()));

            Usuario adminUser = Usuario.builder()
                    .nombre("Super Admin")
                    .correoElectronico("admin@worksync.com")
                    .contrasena(passwordEncoder.encode("123456"))
                    .estado(true)
                    .rol(adminRol)
                    .build();

            usuarioCrudRepository.save(adminUser);
            System.out.println("Usuario administrador por defecto creado con éxito (admin@worksync.com / 123456).");
        }
    }
}
