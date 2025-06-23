package com.furniture.miley.purchase.controller;

import com.furniture.miley.commons.constants.ResponseMessage;
import com.furniture.miley.commons.dto.SuccessResponseDTO;
import com.furniture.miley.exception.customexception.ResourceNotFoundException;
import com.furniture.miley.purchase.dto.purchaseOrder.PurchaseOrderDTO;
import com.furniture.miley.purchase.dto.supplier.CreateSupplierDTO;
import com.furniture.miley.purchase.dto.supplier.SupplierDTO;
import com.furniture.miley.purchase.service.SupplierService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/supplier")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @GetMapping
    public ResponseEntity<SuccessResponseDTO<List<SupplierDTO>>> getAll(){
        List<SupplierDTO> supplierDTOList = supplierService.getAll();
        return supplierDTOList.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.SUCCESS,
                        HttpStatus.OK.name(),
                        supplierDTOList
                )
        );
    }

    @PostMapping
    public ResponseEntity<SuccessResponseDTO<SupplierDTO>> create(
            @RequestBody CreateSupplierDTO createSupplierDTO
            ){
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.CREATED,
                        HttpStatus.OK.name(),
                        supplierService.create( createSupplierDTO )
                )
        );
    }

    @PutMapping
    public ResponseEntity<SuccessResponseDTO<SupplierDTO>> update(
            @RequestBody SupplierDTO supplierDTO
    ) throws ResourceNotFoundException {
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.UPDATED,
                        HttpStatus.OK.name(),
                        supplierService.update( supplierDTO )
                )
        );
    }
}
