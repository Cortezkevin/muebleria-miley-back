package com.furniture.miley.warehouse.controller;

import com.furniture.miley.commons.constants.ResponseMessage;
import com.furniture.miley.commons.dto.SuccessResponseDTO;
import com.furniture.miley.delivery.service.CarrierService;
import com.furniture.miley.exception.customexception.ResourceNotFoundException;
import com.furniture.miley.purchase.dto.purchaseOrder.PurchaseOrderDTO;
import com.furniture.miley.warehouse.dto.carrier.CarrierDTO;
import com.furniture.miley.warehouse.dto.carrier.NewCarrierDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/carrier")
@RequiredArgsConstructor
public class CarrierController {

    private final CarrierService carrierService;

    @PreAuthorize("hasAnyAuthority('ROLE_WAREHOUSE','ROLE_TRANSPORT','ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<SuccessResponseDTO<List<CarrierDTO>>> getAll(){
        List<CarrierDTO> carrierDTOS = carrierService.getAll();
        return carrierDTOS.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.SUCCESS,
                        HttpStatus.OK.name(),
                        carrierDTOS
                )
        );
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<SuccessResponseDTO<CarrierDTO>> create(
            @RequestBody NewCarrierDTO newCarrierDTO
    ) throws ResourceNotFoundException {
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.CREATED,
                        HttpStatus.OK.name(),
                        carrierService.create( newCarrierDTO )
                )
        );
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_TRANSPORT')")
    @PostMapping("/available/{carrierId}")
    public ResponseEntity<SuccessResponseDTO<CarrierDTO>> changeStatus(
            @PathVariable String carrierId
    ) throws ResourceNotFoundException {
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.SUCCESS,
                        HttpStatus.OK.name(),
                        carrierService.availableStatus( carrierId )
                )
        );
    }
}
