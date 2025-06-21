package com.furniture.miley.security.service;

import com.furniture.miley.catalog.model.Product;
import com.furniture.miley.catalog.repository.ProductRepository;
import com.furniture.miley.sales.dto.NewUserDTO;
import com.furniture.miley.sales.dto.ResponseWrapperDTO;
import com.furniture.miley.exception.customexception.ResourceDuplicatedException;
import com.furniture.miley.exception.customexception.ResourceNotFoundException;
import com.furniture.miley.sales.repository.CartItemRepository;
import com.furniture.miley.sales.repository.CartRepository;
import com.furniture.miley.sales.repository.PersonalInformationRepository;
import com.furniture.miley.sales.model.Address;
import com.furniture.miley.sales.model.cart.Cart;
import com.furniture.miley.sales.model.cart.CartItem;
import com.furniture.miley.sales.model.PersonalInformation;
import com.furniture.miley.security.dto.JwtTokenDTO;
import com.furniture.miley.security.dto.UserDTO;
import com.furniture.miley.security.enums.RolName;
import com.furniture.miley.security.jwt.JwtProvider;
import com.furniture.miley.security.model.MainUser;
import com.furniture.miley.security.model.Role;
import com.furniture.miley.security.model.User;
import com.furniture.miley.security.repository.AddressRepository;
import com.furniture.miley.security.repository.RoleRepository;
import com.furniture.miley.security.repository.UserRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class UserService {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository repository;
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

    @SneakyThrows
    public ResponseWrapperDTO<JwtTokenDTO> registerUser(NewUserDTO newUserDTO ){
        try {
            if( repository.existsByEmail(newUserDTO.email()) ) throw new ResourceDuplicatedException(newUserDTO.email() + " ya tiene una cuenta asociada");

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
                    .build();
            User userCreated = repository.save(newUser);


            Cart newCart = Cart.builder()
                    .user(userCreated)
                    .subtotal(BigDecimal.ZERO)
                    .total(BigDecimal.ZERO)
                    .shippingCost(BigDecimal.ZERO)
                    .tax(BigDecimal.ZERO)
                    .discount(BigDecimal.ZERO)
                    .build();

            Cart cartCreated = cartRepository.save( newCart );

            // carga de carrito temporal desde el dto
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
                cartRepository.save(cartCreated);
            }

            Cart cartRecent = cartRepository.findById( cartCreated.getId() ).orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));
            cartRecent.calculateTotals();

            cartRepository.save(cartRecent);

            Address newAddress = Address.builder()
                    .urbanization("")
                    .postalCode(0)
                    .street("")
                    .fullAddress("")
                    .province("")
                    .district("")
                    .department("")
                    .build();

            if(newUserDTO.memoryAddress() != null){
                System.out.println("MEMORY ADDRESS PRESENT");
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
            User userRecent = repository.save( userCreated );


            MainUser mainUser = MainUser.build( userRecent );
            String token = jwtProvider.generateToken( mainUser );

            JwtTokenDTO jwtTokenDTO = new JwtTokenDTO(
                    token,
                    UserDTO.parseToDTO( userRecent, personalInformationCreated )
            );
            return ResponseWrapperDTO.<JwtTokenDTO>builder()
                    .message("Usuario creado satisfactoriamente")
                    .status(HttpStatus.OK.name())
                    .success( true )
                    .content( jwtTokenDTO )
                    .build();
        }catch (Exception e){
            return ResponseWrapperDTO.<JwtTokenDTO>builder()
                    .message("Ocurrio un error: " + e.getMessage())
                    .status(HttpStatus.BAD_REQUEST.name())
                    .success( false )
                    .content( null )
                    .build();
        }
    }
}
