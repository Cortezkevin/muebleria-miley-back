package com.furniture.miley.warehouse.controller;

import com.furniture.miley.commons.constants.ResponseMessage;
import com.furniture.miley.commons.dto.SuccessResponseDTO;
import com.furniture.miley.exception.customexception.InsufficientStockException;
import com.furniture.miley.exception.customexception.ProductAndMaterialNotFoundException;
import com.furniture.miley.exception.customexception.ResourceNotFoundException;
import com.furniture.miley.warehouse.dto.grocer.GrocerDTO;
import com.furniture.miley.warehouse.dto.warehouse.CreateInventoryMovementDTO;
import com.furniture.miley.warehouse.dto.warehouse.DetailedMovementDTO;
import com.furniture.miley.warehouse.dto.warehouse.InventoryMovementsDTO;
import com.furniture.miley.warehouse.dto.warehouse.UpdateInventoryMovementsDTO;
import com.furniture.miley.warehouse.service.InventoryMovementsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/movements")
@RequiredArgsConstructor
public class InventoryMovementsController {

    private final InventoryMovementsService inventoryMovementsService;

    @GetMapping
    public ResponseEntity<SuccessResponseDTO<List<InventoryMovementsDTO>>> getAll(){
        List<InventoryMovementsDTO> inventoryMovementsDTOList = inventoryMovementsService.getAll();
        return inventoryMovementsDTOList.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.SUCCESS,
                        HttpStatus.OK.name(),
                        inventoryMovementsDTOList
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponseDTO<DetailedMovementDTO>> getById(
            @PathVariable String id
    ) throws ResourceNotFoundException {
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.SUCCESS,
                        HttpStatus.OK.name(),
                        inventoryMovementsService.getById(id)
                )
        );
    }

    @PostMapping
    public ResponseEntity<SuccessResponseDTO<List<InventoryMovementsDTO>>> create(
            @RequestBody CreateInventoryMovementDTO createInventoryMovementDTO
            ) throws InsufficientStockException, ProductAndMaterialNotFoundException, ResourceNotFoundException {
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.CREATED,
                        HttpStatus.OK.name(),
                        inventoryMovementsService.create( createInventoryMovementDTO )
                )
        );
    }

    @PutMapping
    public ResponseEntity<SuccessResponseDTO<InventoryMovementsDTO>> update(
            @RequestBody UpdateInventoryMovementsDTO updateInventoryMovementsDTO
    ) throws ProductAndMaterialNotFoundException, ResourceNotFoundException {
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.UPDATED,
                        HttpStatus.OK.name(),
                        inventoryMovementsService.update( updateInventoryMovementsDTO )
                )
        );
    }
}
