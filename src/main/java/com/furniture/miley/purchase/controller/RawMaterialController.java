package com.furniture.miley.purchase.controller;

import com.furniture.miley.commons.constants.ResponseMessage;
import com.furniture.miley.commons.dto.SuccessResponseDTO;
import com.furniture.miley.exception.customexception.ResourceNotFoundException;
import com.furniture.miley.purchase.dto.rawMaterial.CreateRawMaterialDTO;
import com.furniture.miley.purchase.dto.rawMaterial.RawMaterialDTO;
import com.furniture.miley.purchase.service.RawMaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/raw_material")
@RequiredArgsConstructor
public class RawMaterialController {

    private final RawMaterialService rawMaterialService;

    @GetMapping
    public ResponseEntity<SuccessResponseDTO<List<RawMaterialDTO>>> getAll(){
        List<RawMaterialDTO> rawMaterialDTOList = rawMaterialService.getAll();
        return rawMaterialDTOList.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.SUCCESS,
                        HttpStatus.OK.name(),
                        rawMaterialDTOList
                )
        );
    }

    @GetMapping("/bySupplier")
    public ResponseEntity<SuccessResponseDTO<List<RawMaterialDTO>>> getAll(
            @RequestParam(name = "supplier") String supplierId
    ) throws ResourceNotFoundException {
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.SUCCESS,
                        HttpStatus.OK.name(),
                        rawMaterialService.getBySupplier( supplierId )
                )
        );
    }

    @PostMapping
    public ResponseEntity<SuccessResponseDTO<RawMaterialDTO>> create(
            @RequestBody CreateRawMaterialDTO createRawMaterialDTO
    ) throws ResourceNotFoundException {
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.CREATED,
                        HttpStatus.OK.name(),
                        rawMaterialService.create( createRawMaterialDTO )
                )
        );
    }

    @PutMapping
    public ResponseEntity<SuccessResponseDTO<RawMaterialDTO>> update(
            @RequestBody RawMaterialDTO rawMaterialDTO
    ) throws ResourceNotFoundException {
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.UPDATED,
                        HttpStatus.OK.name(),
                        rawMaterialService.update( rawMaterialDTO )
                )
        );
    }

}
