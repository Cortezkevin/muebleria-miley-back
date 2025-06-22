package com.furniture.miley.warehouse.service;

import com.furniture.miley.catalog.model.Product;
import com.furniture.miley.catalog.service.ProductService;
import com.furniture.miley.exception.customexception.InsufficientStockException;
import com.furniture.miley.exception.customexception.ProductAndMaterialNotFoundException;
import com.furniture.miley.exception.customexception.ResourceNotFoundException;
import com.furniture.miley.purchase.model.EntryGuide;
import com.furniture.miley.purchase.model.RawMaterial;
import com.furniture.miley.purchase.repository.EntryGuideRepository;
import com.furniture.miley.purchase.service.RawMaterialService;
import com.furniture.miley.warehouse.dto.warehouse.*;
import com.furniture.miley.warehouse.enums.InventoryMovementType;
import com.furniture.miley.warehouse.model.ExitGuide;
import com.furniture.miley.warehouse.model.Grocer;
import com.furniture.miley.warehouse.model.InventoryMovements;
import com.furniture.miley.warehouse.model.Warehouse;
import com.furniture.miley.warehouse.repository.ExitGuideRepository;
import com.furniture.miley.warehouse.repository.InventoryMovementsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryMovementsService {

    private final InventoryMovementsRepository inventoryMovementsRepository;
    private final EntryGuideRepository entryGuideRepository;
    private final ExitGuideRepository exitGuideRepository;

    private final GrocerService grocerService;
    private final ProductService productService;
    private final RawMaterialService rawMaterialService;
    private final WarehouseService warehouseService;

    public InventoryMovements findById(String id) throws ResourceNotFoundException {
        return inventoryMovementsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento no encontrado","InventoryMovement"));
    }

    public List<InventoryMovementsDTO> getAll(){
        Sort sort = Sort.by(Sort.Direction.DESC, "date");
        return inventoryMovementsRepository.findAll(sort).stream().map(InventoryMovementsDTO::toDTO).toList();
    }

    public DetailedMovementDTO getById(String id) throws ResourceNotFoundException {
        return DetailedMovementDTO.toDTO( this.findById( id ) );
    }

    public List<InventoryMovementsDTO> create(CreateInventoryMovementDTO createInventoryMovementDTO) throws ResourceNotFoundException, InsufficientStockException, ProductAndMaterialNotFoundException {
        Warehouse warehouse = warehouseService.findById( createInventoryMovementDTO.warehouse() );
        Grocer grocer = grocerService.findById( createInventoryMovementDTO.grocerId() );

        ExitGuide exitGuideCreated = null;
        EntryGuide entryGuideCreated = null;
        if( createInventoryMovementDTO.type().equals(InventoryMovementType.ENTRADA) ){
            EntryGuide entryGuide = EntryGuide.builder()
                    .productConditions(createInventoryMovementDTO.conditions())
                    .date(new Timestamp(System.currentTimeMillis()))
                    .warehouse( warehouse )
                    .grocer( grocer )
                    .build();
            entryGuideCreated = entryGuideRepository.save( entryGuide );
        }else {
            ExitGuide exitGuide = ExitGuide.builder()
                    .observations(createInventoryMovementDTO.conditions() )
                    .date(new Timestamp(System.currentTimeMillis()))
                    .warehouse( warehouse )
                    .grocer( grocer )
                    .build();
            exitGuideCreated = exitGuideRepository.save( exitGuide );
        }

        List<InventoryMovements> newInventoryMovementsList = new ArrayList<>();
        // reducir o aumentar cantidad de stock
        for (MaterialOrProductDTO mop: createInventoryMovementDTO.materialOrProducts()){
            InventoryMovements inventoryMovements = InventoryMovements.builder()
                    .type( createInventoryMovementDTO.type() )
                    .reason( createInventoryMovementDTO.reason() )
                    .date(new Timestamp(System.currentTimeMillis()))
                    .warehouse(warehouse)
                    .amount(mop.amount())
                    .exitGuide(exitGuideCreated)
                    .entryGuide(entryGuideCreated)
                    .build();

            try {
                RawMaterial rawMaterial = rawMaterialService.findById( mop.id() );
                inventoryMovements.setRawMaterial( rawMaterial );
                inventoryMovements.setInitialStock( rawMaterial.getStock() );
                int newStock = 0;
                if( createInventoryMovementDTO.type().equals(InventoryMovementType.ENTRADA) ){
                    newStock = rawMaterial.getStock() + mop.amount();
                    //rawMaterial.setStock( rawMaterial.getStock() + mop.amount() );
                }else {
                    if( rawMaterial.getStock() < mop.amount() ){
                        throw new InsufficientStockException("No cuenta con suficientes existencias del material: " + rawMaterial.getName(), "Material");
                    }else {
                        newStock = rawMaterial.getStock() - mop.amount();
                        //rawMaterial.setStock( rawMaterial.getStock() - mop.amount() );
                    }
                }

                inventoryMovements.setNewStock( newStock );
                rawMaterial.setStock( newStock );
                inventoryMovements.setRawMaterial(rawMaterial);
            } catch (ResourceNotFoundException e1) {
                try {
                    Product product = productService.findById(mop.id() );
                    inventoryMovements.setProduct( product );
                    inventoryMovements.setInitialStock( product.getStock() );
                    int newStock = 0;
                    if( createInventoryMovementDTO.type().equals(InventoryMovementType.ENTRADA) ){
                        newStock = product.getStock() + mop.amount();
                        //product.setStock( product.getStock() + mop.amount() );
                    }else {
                        if( product.getStock() < mop.amount() ){
                            throw new InsufficientStockException("No cuenta con suficientes existencias del producto: " + product.getName(), "Product");
                        }else {
                            newStock = product.getStock() - mop.amount();
                            //product.setStock( product.getStock() - mop.amount() );
                        }
                    }

                    inventoryMovements.setNewStock( newStock );
                    product.setStock( newStock );
                    inventoryMovements.setProduct(product);
                } catch (ResourceNotFoundException e2) {
                    throw new ProductAndMaterialNotFoundException("Neither Product nor RawMaterial found for ID: " + mop.id());
                }
            }
            newInventoryMovementsList.add( inventoryMovements );
        }

        return inventoryMovementsRepository.saveAll(newInventoryMovementsList)
                .stream().map(InventoryMovementsDTO::toDTO).toList();
    }

    public InventoryMovementsDTO update(UpdateInventoryMovementsDTO updateInventoryMovementsDTO) throws ResourceNotFoundException, ProductAndMaterialNotFoundException {
        InventoryMovements inventoryMovements = this.findById( updateInventoryMovementsDTO.id() );

        if( updateInventoryMovementsDTO.warehouse() != null ){
            Warehouse warehouse = warehouseService.findById( updateInventoryMovementsDTO.warehouse() );
            inventoryMovements.setWarehouse( warehouse );
        }

        inventoryMovements.setType(updateInventoryMovementsDTO.type());
        inventoryMovements.setProduct( null );
        inventoryMovements.setRawMaterial( null );
        inventoryMovements.setReason(updateInventoryMovementsDTO.reason());

        Product product = null;
        try {
            product = productService.findById( updateInventoryMovementsDTO.productOrMaterialId() );
            inventoryMovements.setProduct( product );
        } catch (ResourceNotFoundException e) {
            try {
                RawMaterial rawMaterial = rawMaterialService.findById( updateInventoryMovementsDTO.productOrMaterialId() );
                inventoryMovements.setRawMaterial( rawMaterial );
            } catch (ResourceNotFoundException ex) {
                throw new ProductAndMaterialNotFoundException("Neither Product nor RawMaterial found for ID: " + updateInventoryMovementsDTO.productOrMaterialId());
            }
        }

        return InventoryMovementsDTO.toDTO( inventoryMovementsRepository.save( inventoryMovements ) );
    }

    public List<InventoryMovements> saveAll(List<InventoryMovements> newInventoryMovementsList) {
        return inventoryMovementsRepository.saveAll(newInventoryMovementsList);
    }
}
