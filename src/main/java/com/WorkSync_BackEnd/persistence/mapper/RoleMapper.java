package com.WorkSync_BackEnd.persistence.mapper;

import com.WorkSync_BackEnd.domain.model.Role;
import com.WorkSync_BackEnd.persistence.entity.Rol;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoleMapper {

    @Mapping(source = "idRol", target = "roleId")
    @Mapping(source = "nombre", target = "name")
    Role toRole(Rol rol);

    @InheritInverseConfiguration
    @Mapping(target = "descripcion", ignore = true)
    Rol toRol(Role role);
}
