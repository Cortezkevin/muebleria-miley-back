package com.furniture.miley.purchase.service;

import com.furniture.miley.exception.customexception.ResourceNotFoundException;
import com.furniture.miley.purchase.dto.supplier.CreateSupplierDTO;
import com.furniture.miley.purchase.dto.supplier.SupplierDTO;
import com.furniture.miley.purchase.model.Supplier;
import com.furniture.miley.purchase.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public Supplier findById(String id) throws ResourceNotFoundException {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado"));
    }

    public List<SupplierDTO> getAll(){
        return supplierRepository.findAll().stream().map(SupplierDTO::toDTO).toList();
    }

    public SupplierDTO create(CreateSupplierDTO createSupplierDTO){
        Supplier newSupplier = Supplier.builder()
                .name(createSupplierDTO.name())
                .address(createSupplierDTO.address())
                .ruc(createSupplierDTO.ruc())
                .phone(createSupplierDTO.phone())
                .build();
        return SupplierDTO.toDTO(supplierRepository.save( newSupplier ));
    }

    public SupplierDTO update(SupplierDTO supplierDTO) throws ResourceNotFoundException {
        Supplier supplier = this.findById( supplierDTO.id() );
        supplier.setName(supplierDTO.name());
        supplier.setAddress(supplierDTO.address());
        supplier.setPhone(supplierDTO.phone());
        supplier.setRuc(supplierDTO.ruc());
        return SupplierDTO.toDTO(supplierRepository.save(supplier));
    }
}
