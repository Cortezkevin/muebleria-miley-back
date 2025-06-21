package com.furniture.miley.security.service;

import com.furniture.miley.catalog.model.Product;
import com.furniture.miley.catalog.repository.ProductRepository;
import com.furniture.miley.commons.constants.ResponseMessage;
import com.furniture.miley.sales.dto.NewUserDTO;
import com.furniture.miley.exception.customexception.ResourceDuplicatedException;
import com.furniture.miley.exception.customexception.ResourceNotFoundException;
import com.furniture.miley.sales.model.Address;
import com.furniture.miley.sales.model.cart.Cart;
import com.furniture.miley.sales.model.cart.CartItem;
import com.furniture.miley.sales.model.PersonalInformation;
import com.furniture.miley.sales.repository.CartItemRepository;
import com.furniture.miley.sales.repository.CartRepository;
import com.furniture.miley.sales.repository.PersonalInformationRepository;
import com.furniture.miley.security.dto.ChangePasswordDTO;
import com.furniture.miley.security.dto.JwtTokenDTO;
import com.furniture.miley.security.dto.LoginUserDTO;
import com.furniture.miley.security.dto.UserDTO;
import com.furniture.miley.security.enums.RolName;
import com.furniture.miley.security.enums.Status;
import com.furniture.miley.security.jwt.JwtProvider;
import com.furniture.miley.security.model.MainUser;
import com.furniture.miley.security.model.Role;
import com.furniture.miley.security.model.User;
import com.furniture.miley.security.repository.AddressRepository;
import com.furniture.miley.security.repository.RoleRepository;
import com.furniture.miley.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final PersonalInformationRepository personalInformationRepository;
    private final AddressRepository addressRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public String createRoles(){
        roleRepository.save(Role.builder().rolName(RolName.ROLE_USER).build());
        roleRepository.save(Role.builder().rolName(RolName.ROLE_ADMIN).build());
        roleRepository.save(Role.builder().rolName(RolName.ROLE_TRANSPORT).build());
        roleRepository.save(Role.builder().rolName(RolName.ROLE_WAREHOUSE).build());
        return ResponseMessage.CREATED;
    }

    @SneakyThrows
    public UserDTO getUserFromToken(String token ){
        String username = jwtProvider.getUsernameFromToken( token );
        User user = userRepository.findByEmail( username ).orElseThrow( () -> new ResourceNotFoundException(ResponseMessage.USER_NOT_FOUND)) ;
        if( user.getStatus().equals(Status.INACTIVO)){
           throw new RuntimeException(ResponseMessage.USER_DISABLED);
        }
        return UserDTO.parseToDTO( user );
    }

    @SneakyThrows
    public JwtTokenDTO loginUser(LoginUserDTO loginUserDTO ){
        User userFound = userRepository.findByEmail(loginUserDTO.email()).orElseThrow(() -> new ResourceNotFoundException("Email no existente"));
        if( userFound.getStatus().equals(Status.INACTIVO)){
            throw new RuntimeException(ResponseMessage.USER_DISABLED);
        }

        boolean validPassword = passwordEncoder.matches(loginUserDTO.password(), userFound.getPassword());
        if( validPassword ){
            Authentication authentication =
                    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userFound.getEmail(), loginUserDTO.password()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            MainUser mainUser = MainUser.build( userFound );

            String token = jwtProvider.generateToken( mainUser );
            return new JwtTokenDTO(
                    token,
                    UserDTO.parseToDTO( userFound , mainUser )
            );
        } else {
            throw new ResourceNotFoundException("Credenciales invalidas");
        }
    }

    @SneakyThrows
    public JwtTokenDTO registerUser(NewUserDTO newUserDTO ){
        if( userRepository.existsByEmail(newUserDTO.email()) ) throw new ResourceDuplicatedException(newUserDTO.email() + " ya tiene una cuenta asociada");

        Role roleAdmin = roleRepository.findByRolName( RolName.ROLE_ADMIN ).orElseThrow(() -> new ResourceNotFoundException(("Rol admin no existe")));
        Role roleUser = roleRepository.findByRolName( RolName.ROLE_USER ).orElseThrow(() -> new ResourceNotFoundException(("Rol user no existe")));

        Set<Role> roles = new HashSet<>();
        roles.add( roleUser );

        if( newUserDTO.isAdmin() != null && newUserDTO.isAdmin() ) {
            roles.add( roleAdmin );
        }

        User newUser = User.builder()
                .email(newUserDTO.email())
                .password(passwordEncoder.encode(newUserDTO.password()))
                .roles( roles )
                .status(Status.ACTIVO)
                .build();
        User userCreated = userRepository.save(newUser);


        Cart newCart = Cart.createEmpty();
        newCart.setUser( userCreated );

        Cart cartCreated = cartRepository.save( newCart );


        if(newUserDTO.memoryCart() != null){
            List<CartItem> cartItemList = new ArrayList<>();
            newUserDTO.memoryCart().itemList().forEach( i -> {
                Product product = productRepository.findById( i.productId() ).orElse(null);
                if( product != null ){
                    CartItem cartItem = CartItem.builder()
                            .product( product )
                            .cart(cartCreated)
                            .total( product.getPrice().multiply( BigDecimal.valueOf(i.amount()) ) )
                            .amount(i.amount())
                            .build();
                    cartItemList.add( cartItem );
                }
            });

            List<CartItem> cartItems = cartItemRepository.saveAll( cartItemList );
            cartCreated.setCartItems( cartItems );
            cartCreated.setShippingCost( newUserDTO.memoryCart().shippingCost() );
            cartRepository.save(cartCreated);

        }

        Cart cartRecent = cartRepository.findById( cartCreated.getId() ).orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));

        cartRecent.calculateTotals();

        cartRepository.save(cartRecent);

        Address newAddress = Address.createEmpty();

        if(newUserDTO.memoryAddress() != null){
            newAddress.setFullAddress( newUserDTO.memoryAddress().fullAddress() );
            newAddress.setProvince( newUserDTO.memoryAddress().province() );
            newAddress.setDistrict( newUserDTO.memoryAddress().district() );
            newAddress.setDepartment( newUserDTO.memoryAddress().department() );
            newAddress.setStreet( newUserDTO.memoryAddress().street() );
            newAddress.setUrbanization( newUserDTO.memoryAddress().urbanization() );
            newAddress.setPostalCode( newUserDTO.memoryAddress().postalCode() );
            newAddress.setLng( newUserDTO.memoryAddress().lng() );
            newAddress.setLta( newUserDTO.memoryAddress().lta() );
        }

        Address addressCreated = addressRepository.save( newAddress );

        PersonalInformation newPersonalInformation = PersonalInformation.builder()
                .firstName(newUserDTO.firstName())
                .lastName(newUserDTO.lastName())
                .phone("")
                .user(userCreated)
                .address(addressCreated)
                .build();

        PersonalInformation personalInformationCreated = personalInformationRepository.save( newPersonalInformation );

        userCreated.setPersonalInformation( personalInformationCreated );
        User userRecent = userRepository.save( userCreated );


        MainUser mainUser = MainUser.build( userRecent );
        String token = jwtProvider.generateToken( mainUser );

        return new JwtTokenDTO(
                token,
                UserDTO.parseToDTO( userRecent, personalInformationCreated )
        );
    }

    public String changePassword(ChangePasswordDTO dto) throws ResourceNotFoundException {
        User user = userRepository.findByTokenPassword( dto.tokenPassword() ).orElseThrow(() -> new ResourceNotFoundException("El token ingresado no es valido"));
        if( dto.password().equals(dto.confirmPassword())){
            user.setPassword(passwordEncoder.encode( dto.password()));
            user.setTokenPassword(null);
            userRepository.save( user );
            return "Se actualizo su contrasena";
        }else {
            return "Las contraseñas no coincidem";
        }
    }
}
