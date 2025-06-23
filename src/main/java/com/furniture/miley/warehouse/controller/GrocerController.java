package com.furniture.miley.warehouse.controller;

import com.furniture.miley.commons.constants.ResponseMessage;
import com.furniture.miley.commons.dto.SuccessResponseDTO;
import com.furniture.miley.exception.customexception.ResourceNotFoundException;
import com.furniture.miley.warehouse.dto.carrier.CarrierDTO;
import com.furniture.miley.warehouse.dto.grocer.GrocerDTO;
import com.furniture.miley.warehouse.dto.grocer.NewGrocerDTO;
import com.furniture.miley.warehouse.service.GrocerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/grocer")
@RequiredArgsConstructor
public class GrocerController {

    private final GrocerService grocerService;

    @PreAuthorize("hasAnyAuthority('ROLE_WAREHOUSE','ROLE_TRANSPORT','ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<SuccessResponseDTO<List<GrocerDTO>>> getAll(){
        List<GrocerDTO> grocerDTOList = grocerService.getAll();
        return grocerDTOList.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.SUCCESS,
                        HttpStatus.OK.name(),
                        grocerDTOList
                )
        );
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<SuccessResponseDTO<GrocerDTO>> create(
            @RequestBody NewGrocerDTO newGrocerDTO
    ) throws ResourceNotFoundException {
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.CREATED,
                        HttpStatus.OK.name(),
                        grocerService.create( newGrocerDTO )
                )
        );
    }

}
