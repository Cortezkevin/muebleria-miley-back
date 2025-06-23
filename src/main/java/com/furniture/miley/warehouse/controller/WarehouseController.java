package com.furniture.miley.warehouse.controller;

import com.furniture.miley.commons.constants.ResponseMessage;
import com.furniture.miley.commons.dto.SuccessResponseDTO;
import com.furniture.miley.exception.customexception.ResourceNotFoundException;
import com.furniture.miley.warehouse.dto.carrier.CarrierDTO;
import com.furniture.miley.warehouse.dto.warehouse.WarehouseDTO;
import com.furniture.miley.warehouse.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/warehouse")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @GetMapping
    public ResponseEntity<SuccessResponseDTO<List<WarehouseDTO>>> getAll(){
        List<WarehouseDTO> warehouseDTOList = warehouseService.getAll();
        return warehouseDTOList.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.SUCCESS,
                        HttpStatus.OK.name(),
                        warehouseDTOList
                )
        );
    }

    @PostMapping
    public ResponseEntity<SuccessResponseDTO<WarehouseDTO>> create(
            @RequestParam(name = "location") String location
    ){
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.CREATED,
                        HttpStatus.OK.name(),
                        warehouseService.create( location )
                )
        );
    }

    @PutMapping
    public ResponseEntity<SuccessResponseDTO<WarehouseDTO>> update(
            @RequestBody WarehouseDTO warehouseDTO
    ) throws ResourceNotFoundException {
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.UPDATED,
                        HttpStatus.OK.name(),
                        warehouseService.update( warehouseDTO )
                )
        );
    }

}
