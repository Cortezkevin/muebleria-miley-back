package com.furniture.miley.delivery.service;

import com.furniture.miley.delivery.enums.CarrierStatus;
import com.furniture.miley.delivery.model.Carrier;
import com.furniture.miley.delivery.repository.CarrierRepository;
import com.furniture.miley.exception.customexception.ResourceNotFoundException;
import com.furniture.miley.security.enums.RolName;
import com.furniture.miley.security.model.Role;
import com.furniture.miley.security.model.User;
import com.furniture.miley.security.service.RoleService;
import com.furniture.miley.security.service.UserService;
import com.furniture.miley.warehouse.dto.carrier.CarrierDTO;
import com.furniture.miley.warehouse.dto.carrier.NewCarrierDTO;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CarrierService {

    private final CarrierRepository carrierRepository;

    private final UserService userService;
    private final RoleService roleService;

    public Carrier save(Carrier carrier) {
        return carrierRepository.save(carrier);
    }

    public Carrier findById(String id) throws ResourceNotFoundException {
        return carrierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Repartidor no encontrado"));
    }

    public List<Carrier> findAll(){
        return carrierRepository.findAll();
    }

    public Carrier findByUser(User user) throws ResourceNotFoundException {
        return carrierRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Repartidor no encontrado"));
    }

    public List<CarrierDTO> getAll(){
        return carrierRepository.findAll().stream().map(CarrierDTO::toDTO).toList();
    }

    public CarrierDTO availableStatus(String carrierId) throws ResourceNotFoundException {
        Carrier carrier = this.findById(carrierId);
        if( !carrier.getStatus().equals(CarrierStatus.EN_DESCANSO) ){
            throw new RuntimeException("Debes estar en descanso para cambiar a disponible");
        }
        carrier.setStatus( CarrierStatus.DISPONIBLE );
        Carrier carrierUpdated = carrierRepository.save( carrier );
        /*"Estado actualizado"*/
        return CarrierDTO.toDTO( carrierUpdated );
    }

    public CarrierDTO create(NewCarrierDTO newCarrierDTO) throws ResourceNotFoundException {
        User user = userService.findById(newCarrierDTO.userId());
        Role role = roleService.findByRolName(RolName.ROLE_TRANSPORT);
        Set<Role> roles = user.getRoles();
        roles.add( role );

        Carrier newCarrier = Carrier.builder()
                .user(user)
                .codePlate(newCarrierDTO.plateCode())
                .status(newCarrierDTO.status())
                .build();

        user.setRoles( roles );
        Carrier carrierCreated = carrierRepository.save( newCarrier );
            /*user.setCarrier( carrierCreated );
            userRepository.save( user );
*/
        return CarrierDTO.toDTO(carrierCreated);
    }
}
