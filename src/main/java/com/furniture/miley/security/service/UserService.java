package com.furniture.miley.security.service;

import com.furniture.miley.commons.helpers.StripeHelpers;
import com.furniture.miley.delivery.service.CarrierService;
import com.furniture.miley.exception.customexception.ResourceDuplicatedException;
import com.furniture.miley.exception.customexception.ResourceNotFoundException;
import com.furniture.miley.profile.dto.user.CreateUserDTO;
import com.furniture.miley.profile.dto.user.UpdateUserDTO;
import com.furniture.miley.profile.model.Address;
import com.furniture.miley.profile.model.PersonalInformation;
import com.furniture.miley.profile.repository.AddressRepository;
import com.furniture.miley.profile.repository.PersonalInformationRepository;
import com.furniture.miley.sales.model.cart.Cart;
import com.furniture.miley.sales.repository.cart.CartRepository;
import com.furniture.miley.security.dto.MinimalUserDTO;
import com.furniture.miley.security.dto.UserDTO;
import com.furniture.miley.security.enums.ResourceStatus;
import com.furniture.miley.security.enums.RolName;
import com.furniture.miley.security.enums.UserStatus;
import com.furniture.miley.security.model.MainUser;
import com.furniture.miley.security.model.Role;
import com.furniture.miley.security.model.User;
import com.furniture.miley.security.repository.UserRepository;
import com.furniture.miley.warehouse.dto.carrier.CarrierDTO;
import com.furniture.miley.warehouse.dto.grocer.GrocerDTO;
import com.furniture.miley.warehouse.service.GrocerService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final RoleService roleService;
    private final UserRepository mRepository;
    private final PasswordEncoder passwordEncoder;
    private final AddressRepository addressRepository;
    private final PersonalInformationRepository personalInformationRepository;
    private final CartRepository cartRepository;

    public User findById(String id) throws ResourceNotFoundException {
        return mRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado","User"));
    }

    public User findByEmail(String email) throws ResourceNotFoundException {
        return mRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado","User"));
    }

    public User save(User user){
        return mRepository.save(user);
    }

    public List<User> saveAll(List<User> users) {
        return mRepository.saveAll(users);
    }

    public List<MinimalUserDTO> getAll(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MainUser mainUser = (MainUser) authentication.getPrincipal();
        return mRepository.findAll().stream()
                .filter(u -> !u.getEmail().equals(mainUser.getEmail()))
                .map(MinimalUserDTO::toDTO).toList();
    }


    public UserDTO create(CreateUserDTO createUserDTO) throws ResourceNotFoundException, ResourceDuplicatedException, StripeException {
        if( mRepository.existsByEmail(createUserDTO.email()) ) throw new ResourceDuplicatedException(createUserDTO.email() + " ya tiene una cuenta asociada");

        Set<Role> roles = new HashSet<>();

        for(String r: createUserDTO.roles()){
            Role role = roleService.findByRolName( RolName.valueOf( r ) );
            roles.add( role );
        }

        User newUser = User.builder()
                .email(createUserDTO.email())
                .password(passwordEncoder.encode(createUserDTO.password()))
                .roles( roles )
                .userStatus(createUserDTO.userStatus())
                .build();

        User userCreated = mRepository.save(newUser);

        Cart newCart = Cart.createEmpty();
        newCart.setUser( userCreated );

        Cart cartCreated = cartRepository.save( newCart );

        Address addressCreated = addressRepository.save( Address.createEmpty() );

        PersonalInformation newPersonalInformation = PersonalInformation.builder()
                .firstName(createUserDTO.firstName())
                .lastName(createUserDTO.lastName())
                .phone("")
                .user(userCreated)
                .address(addressCreated)
                .build();

        PersonalInformation personalInformationCreated = personalInformationRepository.save( newPersonalInformation );

        userCreated.setPersonalInformation( personalInformationCreated );
        userCreated.setCart( cartCreated );

        userCreated.setClientId(StripeHelpers.createStripeClient(userCreated));

        User userRecent = mRepository.save( userCreated );

        return UserDTO.toDTO( userRecent );
    }


    public UserDTO update(UpdateUserDTO updateUserDTO) throws ResourceNotFoundException {
        User user = this.findById( updateUserDTO.userId() );
        user.setEmail( updateUserDTO.email() );

        PersonalInformation personalInformation = personalInformationRepository.findByUser( user ).orElse(null);
        if( personalInformation != null ){
            personalInformation.setFirstName(updateUserDTO.firstName());
            personalInformation.setLastName(updateUserDTO.lastName());
            PersonalInformation personalInformationUpdated = personalInformationRepository.save(personalInformation);
            user.setPersonalInformation( personalInformationUpdated );
        }

        Set<Role> roles = new HashSet<>();

        for(String r: updateUserDTO.roles()){
            Role roleUser = roleService.findByRolName( RolName.valueOf( r ) );
            roles.add( roleUser );
        }

        user.setRoles( roles );

        return UserDTO.toDTO(mRepository.save( user ));
    }

    public Object getExtraRoleData() throws ResourceNotFoundException {
        MainUser mainUser = (MainUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = this.findByEmail(mainUser.getEmail());
        if(user.getGrocer() != null){
            return GrocerDTO.toDTO(user.getGrocer());
        }else if(user.getCarrier() != null) {
            return CarrierDTO.toDTO(user.getCarrier());
        }else {
            return null;
        }
    }

    public void logicalDelete(String id) throws ResourceNotFoundException {
        User user = this.findById(id);
        if(user.getResourceStatus().equals(ResourceStatus.DELETED)) throw new RuntimeException("The user " + user.getEmail() + " is already deleted");
        user.setUserStatus(UserStatus.INACTIVO);
        user.setResourceStatus(ResourceStatus.DELETED);
        mRepository.save(user);
    }

    public void restore(String id) throws ResourceNotFoundException {
        User user = this.findById(id);
        if(user.getResourceStatus().equals(ResourceStatus.ACTIVE)) throw new RuntimeException("The user " + user.getEmail() + " is not deleted");
        user.setResourceStatus(ResourceStatus.ACTIVE);
        mRepository.save(user);
    }

    public void enable(String id) throws ResourceNotFoundException {
        User user = this.findById(id);
        if(user.getUserStatus().equals(UserStatus.ACTIVO)) throw new RuntimeException("The user " + user.getEmail() + " is already enabled");
        user.setUserStatus(UserStatus.ACTIVO);
        mRepository.save(user);
    }

    public void disable(String id) throws ResourceNotFoundException {
        User user = this.findById(id);
        if(user.getUserStatus().equals(UserStatus.INACTIVO)) throw new RuntimeException("The user " + user.getEmail() + " is already active");
        user.setUserStatus(UserStatus.INACTIVO);
        mRepository.save(user);
    }
}
