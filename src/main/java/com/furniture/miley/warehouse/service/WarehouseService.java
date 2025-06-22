package com.furniture.miley.warehouse.service;

import com.furniture.miley.exception.customexception.ResourceNotFoundException;
import com.furniture.miley.warehouse.dto.warehouse.WarehouseDTO;
import com.furniture.miley.warehouse.model.Warehouse;
import com.furniture.miley.warehouse.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;

    public Warehouse findById(String id) throws ResourceNotFoundException {
        return warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Almacen no encontrado"));
    }

    public List<WarehouseDTO> getAll(){
        return warehouseRepository.findAll().stream().map( WarehouseDTO::toDTO ).toList();
    }

    public WarehouseDTO create(String location){
        Warehouse newWarehouse = Warehouse.builder()
                .location( location )
                .build();
        Warehouse warehouse = warehouseRepository.save( newWarehouse );
        return WarehouseDTO.toDTO(warehouse);
    }

    public WarehouseDTO update(WarehouseDTO warehouseDTO) throws ResourceNotFoundException {
        Warehouse warehouse = this.findById( warehouseDTO.id() );
        warehouse.setLocation(warehouseDTO.location());
        return WarehouseDTO.toDTO(warehouseRepository.save(warehouse));
    }
}
