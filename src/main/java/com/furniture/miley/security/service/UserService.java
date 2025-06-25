package com.furniture.miley.security.service;

import com.furniture.miley.catalog.repository.ProductRepository;
import com.furniture.miley.commons.helpers.StripeHelpers;
import com.furniture.miley.profile.dto.user.CreateUserDTO;
import com.furniture.miley.profile.dto.user.UpdateUserDTO;
import com.furniture.miley.profile.repository.AddressRepository;
import com.furniture.miley.exception.customexception.ResourceDuplicatedException;
import com.furniture.miley.exception.customexception.ResourceNotFoundException;
import com.furniture.miley.profile.repository.PersonalInformationRepository;
import com.furniture.miley.sales.repository.cart.CartItemRepository;
import com.furniture.miley.sales.repository.cart.CartRepository;
import com.furniture.miley.profile.model.Address;
import com.furniture.miley.sales.model.cart.Cart;
import com.furniture.miley.profile.model.PersonalInformation;
import com.furniture.miley.security.dto.UserDTO;
import com.furniture.miley.security.enums.RolName;
import com.furniture.miley.security.jwt.JwtProvider;
import com.furniture.miley.security.model.MainUser;
import com.furniture.miley.security.model.Role;
import com.furniture.miley.security.model.User;
import com.furniture.miley.security.repository.UserRepository;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class UserService {

    @Autowired
    private RoleService roleService;
    @Autowired
    private UserRepository mRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private PersonalInformationRepository personalInformationRepository;
    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartItemRepository cartItemRepository;

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

    public List<UserDTO> getAll(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MainUser mainUser = (MainUser) authentication.getPrincipal();
        return mRepository.findAll().stream()
                .filter(u -> !u.getEmail().equals(mainUser.getEmail()))
                .map(UserDTO::toDTO).toList();
    }


    public UserDTO create(CreateUserDTO createUserDTO) throws ResourceNotFoundException, ResourceDuplicatedException, StripeException {
        if( mRepository.existsByEmail(createUserDTO.email()) ) throw new ResourceDuplicatedException(createUserDTO.email() + " ya tiene una cuenta asociada");

        Set<Role> roles = new HashSet<>();

        for(String r: createUserDTO.roles()){
            Role roleUser = roleService.findByRolName( RolName.valueOf( r ) );
            roles.add( roleUser );
        }

        User newUser = User.builder()
                .email(createUserDTO.email())
                .password(passwordEncoder.encode(createUserDTO.password()))
                .roles( roles )
                .status(createUserDTO.status())
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
        user.setStatus( updateUserDTO.status() );

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

}
