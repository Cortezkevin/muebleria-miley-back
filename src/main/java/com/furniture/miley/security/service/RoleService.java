package com.furniture.miley.security.service;

import com.furniture.miley.exception.customexception.ResourceNotFoundException;
import com.furniture.miley.security.dto.RoleDTO;
import com.furniture.miley.security.enums.RolName;
import com.furniture.miley.security.model.Role;
import com.furniture.miley.security.repository.RoleRepository;
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

    public Role findByRolName(RolName rolName) throws ResourceNotFoundException {
        return repository.findByRolName(rolName)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado"));
    }

}
