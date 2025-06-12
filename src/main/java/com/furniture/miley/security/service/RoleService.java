package com.furniture.miley.security.service;

import com.furniture.miley.security.dto.RoleDTO;
import com.furniture.miley.security.enums.RolName;
import com.furniture.miley.security.model.Role;
import com.furniture.miley.security.repository.RoleRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RoleService {

    @Autowired
    private RoleRepository repository;

    public List<RoleDTO> getRoles(){
        return repository.findAll().stream().map(RoleDTO::parseToDTO).toList();
    }

    @SneakyThrows
    public Role getByRolName(RolName rolName){
        Role role = repository.findByRolName(rolName).orElse(null);
        if( role != null ){
            return role;
        }
        throw new Exception("Rol no encontrado con nombre: " + rolName.name());
    }

}
