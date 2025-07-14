package com.furniture.miley.warehouse.service;

import com.furniture.miley.exception.customexception.ResourceNotFoundException;
import com.furniture.miley.security.enums.RolName;
import com.furniture.miley.security.model.Role;
import com.furniture.miley.security.model.User;
import com.furniture.miley.security.repository.RoleRepository;
import com.furniture.miley.security.repository.UserRepository;
import com.furniture.miley.security.service.RoleService;
import com.furniture.miley.security.service.UserService;
import com.furniture.miley.warehouse.dto.grocer.GrocerDTO;
import com.furniture.miley.warehouse.dto.grocer.NewGrocerDTO;
import com.furniture.miley.warehouse.enums.GrocerStatus;
import com.furniture.miley.warehouse.model.Grocer;
import com.furniture.miley.warehouse.repository.GrocerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GrocerService {

    private final GrocerRepository grocerRepository;
    private final UserService userService;
    private final RoleService roleService;

    public List<Grocer> findAll(){
        return grocerRepository.findAll();
    }

    public Grocer findById(String id) throws ResourceNotFoundException {
        return grocerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Almacenero no encontrado","Grocer"));
    }

    public Grocer save(Grocer grocer){
        return grocerRepository.save(grocer);
    }

    public List<GrocerDTO> getAll(){
        return this.findAll().stream().map(GrocerDTO::toDTO).toList();
    }

    public GrocerDTO create(NewGrocerDTO newGrocerDTO) throws ResourceNotFoundException {
        User user = userService.findById(newGrocerDTO.userId());
        Role role = roleService.findByRolName(RolName.ROLE_WAREHOUSE);

        Set<Role> roles = user.getRoles();
        roles.add( role );

        Grocer newGrocer = Grocer.builder()
                .user(user)
                .status(GrocerStatus.DISPONIBLE)
                .build();

        user.setRoles( roles );
        newGrocer.setUser(user);
        return GrocerDTO.toDTO(grocerRepository.save( newGrocer ));
    }

    public Grocer findByUser(User user) throws ResourceNotFoundException {
        return grocerRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Este usuario no es un almacenero"));
    }
}