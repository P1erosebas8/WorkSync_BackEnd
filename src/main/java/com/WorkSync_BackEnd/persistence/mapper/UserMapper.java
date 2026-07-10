package com.WorkSync_BackEnd.persistence.mapper;

import com.WorkSync_BackEnd.domain.model.User;
import com.WorkSync_BackEnd.persistence.entity.Usuario;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {RoleMapper.class})
public interface UserMapper {

    @Mapping(source = "idUsuario", target = "userId")
    @Mapping(source = "nombre", target = "name")
    @Mapping(source = "correoElectronico", target = "email")
    @Mapping(source = "contrasena", target = "password")
    @Mapping(source = "estado", target = "active")
    @Mapping(source = "rol", target = "role")
    User toUser(Usuario usuario);

    List<User> toUsers(List<Usuario> usuarios);

    @InheritInverseConfiguration
    Usuario toUsuario(User user);
}
