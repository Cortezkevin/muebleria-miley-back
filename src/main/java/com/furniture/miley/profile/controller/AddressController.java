package com.furniture.miley.profile.controller;

import com.furniture.miley.commons.constants.ResponseMessage;
import com.furniture.miley.commons.dto.SuccessResponseDTO;
import com.furniture.miley.exception.customexception.ResourceNotFoundException;
import com.furniture.miley.profile.dto.address.AddressDTO;
import com.furniture.miley.profile.service.AddressService;
import com.furniture.miley.sales.dto.cart.CartDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping("/fromSession")
    public ResponseEntity<SuccessResponseDTO<AddressDTO>> getAddressFromSession() throws ResourceNotFoundException {
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.PRODUCT_ADDED_TO_CART,
                        HttpStatus.OK.name(),
                        addressService.getFromSession()
                )
        );
    }

    @PutMapping
    public ResponseEntity<SuccessResponseDTO<AddressDTO>> updateAddress(
            @RequestBody AddressDTO addressDTO
    ) throws ResourceNotFoundException {
        return ResponseEntity.ok(
          new SuccessResponseDTO<>(
                  ResponseMessage.UPDATED,
                  HttpStatus.OK.name(),
                  addressService.updateAddress( addressDTO )
          )
        );
    }

}
