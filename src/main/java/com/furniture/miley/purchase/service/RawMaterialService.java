package com.furniture.miley.purchase.service;

import com.furniture.miley.exception.customexception.ResourceNotFoundException;
import com.furniture.miley.purchase.dto.rawMaterial.CreateRawMaterialDTO;
import com.furniture.miley.purchase.dto.rawMaterial.RawMaterialDTO;
import com.furniture.miley.purchase.model.RawMaterial;
import com.furniture.miley.purchase.model.Supplier;
import com.furniture.miley.purchase.repository.RawMaterialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RawMaterialService {

    private final RawMaterialRepository rawMaterialRepository;

    private final SupplierService supplierService;

    public RawMaterial findById(String id) throws ResourceNotFoundException {
        return rawMaterialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Material no encontrado", "Material"));
    }

    public List<RawMaterialDTO> getAll(){
        return rawMaterialRepository.findAll().stream().map(RawMaterialDTO::toDTO).toList();
    }

    public List<RawMaterialDTO> getBySupplier(String supplierId) throws ResourceNotFoundException {
        Supplier supplier = supplierService.findById( supplierId );
        return rawMaterialRepository.findBySupplier( supplier ).stream().map(RawMaterialDTO::toDTO).toList();
    }

    public RawMaterialDTO create(CreateRawMaterialDTO createRawMaterialDTO) throws ResourceNotFoundException {
        Supplier supplier = supplierService.findById(createRawMaterialDTO.supplierId());
        RawMaterial newRawMaterial = RawMaterial.builder()
                .description(createRawMaterialDTO.description())
                .name(createRawMaterialDTO.name())
                .measurementUnit(createRawMaterialDTO.measurementUnit())
                .unitPrice(createRawMaterialDTO.unitPrice())
                .supplier(supplier)
                .stock(0)
                .build();
        return RawMaterialDTO.toDTO(rawMaterialRepository.save( newRawMaterial ));
    }

    public RawMaterialDTO update(RawMaterialDTO rawMaterialDTO) throws ResourceNotFoundException {
        RawMaterial rawMaterial = this.findById( rawMaterialDTO.id() );
        rawMaterial.setName(rawMaterialDTO.name());
        rawMaterial.setDescription(rawMaterialDTO.description());
        rawMaterial.setUnitPrice(rawMaterialDTO.unitPrice());
        rawMaterial.setMeasurementUnit(rawMaterialDTO.measurementUnit());

        if( rawMaterial.getSupplier() != null || rawMaterial.getSupplier().getId() != rawMaterialDTO.id()){
            Supplier supplier = supplierService.findById(rawMaterialDTO.supplierId());
            rawMaterial.setSupplier( supplier );
        }

        return RawMaterialDTO.toDTO(rawMaterialRepository.save( rawMaterial ));
    }
}
