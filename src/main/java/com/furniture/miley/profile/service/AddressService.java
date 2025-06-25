package com.furniture.miley.profile.service;

import com.furniture.miley.exception.customexception.ResourceNotFoundException;
import com.furniture.miley.profile.dto.address.AddressDTO;

import com.furniture.miley.profile.model.Address;
import com.furniture.miley.profile.model.PersonalInformation;
import com.furniture.miley.profile.repository.AddressRepository;
import com.furniture.miley.security.model.User;
import com.furniture.miley.security.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final PersonalInformationService personalInformationService;
    private final UserService userService;
    private final AddressRepository addressRepository;

    public Address findById(String id) throws ResourceNotFoundException {
        return addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Direccion no encontrada","Address"));
    }
    public Address findByPersonalInformation(PersonalInformation personalInformation) throws ResourceNotFoundException {
        return addressRepository.findByPersonalInformation(personalInformation)
                .orElseThrow(() -> new ResourceNotFoundException("Direccion no encontrada","Address"));
    }


    /*public AddressDTO create(NewAddressDTO newAddressDTO) throws ResourceNotFoundException {
        try {
            User user = userService.findById(newAddressDTO.userId() );
            PersonalInformation personalInformation = personalInformationService.findByUser( user );

            Address newAddress = Address.builder()
                    .lng(newAddressDTO.lng())
                    .lta(newAddressDTO.lta())
                    .department(newAddressDTO.department())
                    .district(newAddressDTO.district())
                    .province(newAddressDTO.province())
                    .street(newAddressDTO.street())
                    .postalCode(newAddressDTO.postalCode())
                    .urbanization(newAddressDTO.urbanization())
                    .fullAddress(newAddressDTO.fullAddress())
                    .build();

            Address addressCreated = addressRepository.save( newAddress );

            personalInformation.setAddress( addressCreated );

            PersonalInformation personalInformationUpdated = personalInformationRepository.save( personalInformation );

            return ResponseWrapperDTO.<AddressDTO>builder()
                    .message("Direccion creada")
                    .status(HttpStatus.OK.name())
                    .success(true)
                    .content(AddressDTO.parseToDTO( addressCreated ))
                    .build();
        }catch ( ResourceNotFoundException e ){
            return ResponseWrapperDTO.<AddressDTO>builder()
                    .success(false)
                    .status(HttpStatus.BAD_REQUEST.name())
                    .message("Ocurrio un error: " + e.getMessage())
                    .content(null)
                    .build();
        }
    }*/

    public AddressDTO updateAddress( AddressDTO addressDTO ) throws ResourceNotFoundException {
        Address address = this.findById( addressDTO.id() );

        address.setDepartment(addressDTO.department());
        address.setProvince(addressDTO.province());
        address.setDistrict(addressDTO.district());
        address.setUrbanization(addressDTO.urbanization());
        address.setStreet(addressDTO.street());
        address.setLng(addressDTO.lng());
        address.setLta(addressDTO.lta());
        address.setPostalCode(addressDTO.postalCode());
        address.setFullAddress(addressDTO.fullAddress());

        return AddressDTO.toDTO( addressRepository.save( address ) );
    }

    public AddressDTO getAddressFromUser(String userId) throws ResourceNotFoundException {
        User user = userService.findById( userId );
        PersonalInformation personalInformation = personalInformationService.findByUser( user );
        Address address = this.findByPersonalInformation( personalInformation );
        return AddressDTO.toDTO( address );
    }
}
