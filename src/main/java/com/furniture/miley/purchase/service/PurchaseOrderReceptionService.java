package com.furniture.miley.purchase.service;

import com.furniture.miley.catalog.model.Product;
import com.furniture.miley.catalog.service.ProductService;
import com.furniture.miley.exception.customexception.AbortedProcessException;
import com.furniture.miley.exception.customexception.PrevStatusRequiredException;
import com.furniture.miley.exception.customexception.ResourceNotFoundException;
import com.furniture.miley.purchase.dto.purchaseOrder.AcceptAndRejectPurchaseOrderDTO;
import com.furniture.miley.purchase.dto.purchaseOrder.DetailedPurchaseOrderReceptionDTO;
import com.furniture.miley.purchase.dto.purchaseOrder.PurchaseOrderReceptionDTO;
import com.furniture.miley.purchase.enums.PurchaseOrderDetailStatus;
import com.furniture.miley.purchase.enums.PurchaseOrderReceptionStatus;
import com.furniture.miley.purchase.enums.PurchaseOrderStatus;
import com.furniture.miley.purchase.model.*;
import com.furniture.miley.purchase.repository.*;
import com.furniture.miley.warehouse.enums.GrocerStatus;
import com.furniture.miley.warehouse.enums.InventoryMovementType;
import com.furniture.miley.warehouse.model.Grocer;
import com.furniture.miley.warehouse.model.InventoryMovements;
import com.furniture.miley.warehouse.model.Warehouse;
import com.furniture.miley.warehouse.repository.InventoryMovementsRepository;
import com.furniture.miley.warehouse.service.GrocerService;
import com.furniture.miley.warehouse.service.InventoryMovementsService;
import com.furniture.miley.warehouse.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseOrderReceptionService {

    private final PurchaseOrderReceptionRepository purchaseOrderReceptionRepository;

    private final GrocerService grocerService;
    private final PurchaseOrderService purchaseOrderService;
    private final PurchaseOrderDetailRepository purchaseOrderDetailRepository;
    private final EntryGuideRepository entryGuideRepository;
    private final RejectionGuideRepository rejectionGuideRepository;
    private final InventoryMovementsService inventoryMovementsService;
    private final RawMaterialService rawMaterialService;
    private final ProductService productService;
    private final WarehouseService warehouseService;

    public PurchaseOrderReception findById(String id) throws ResourceNotFoundException {
        return purchaseOrderReceptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recepcion de orden de compra no encontrado", "PurchaseOrderReception"));
    }

    public DetailedPurchaseOrderReceptionDTO getDetailsById(String id) throws ResourceNotFoundException {
        return DetailedPurchaseOrderReceptionDTO.toDTO(this.findById( id ));
    }

    public List<PurchaseOrderReceptionDTO> getAll(){
        return purchaseOrderReceptionRepository.findAll()
                .stream().map(PurchaseOrderReceptionDTO::toDTO).toList();
    }

    public DetailedPurchaseOrderReceptionDTO startOrderReception(String purchaseOrderId, String receptionId, String grocerId) throws ResourceNotFoundException, AbortedProcessException {
        PurchaseOrder purchaseOrder = purchaseOrderService.findById( purchaseOrderId );

        if( purchaseOrder.getStatus().equals(PurchaseOrderStatus.CANCELADA)){
            throw new AbortedProcessException("La orden de compra fue cancelada/anulada", "PurchaceOrderReception");
        }

        Grocer grocer = grocerService.findById( grocerId );

        if( !grocer.getStatus().equals(GrocerStatus.DISPONIBLE)) {
            throw new PrevStatusRequiredException("Para iniciar la recepcion de la compra debe estar previamente disponible.", GrocerStatus.DISPONIBLE.name());
        }

        grocer.setStatus( GrocerStatus.RECEPCIONANDO_ORDEN );

        PurchaseOrderReception purchaseOrderReception = this.findById( receptionId );
        purchaseOrderReception.setStartDate( new Timestamp(System.currentTimeMillis()));
        purchaseOrderReception.setGrocer( grocer );
        purchaseOrderReception.setStatus( PurchaseOrderReceptionStatus.RECIBIDO );

        purchaseOrder.setStatus( PurchaseOrderStatus.RECIBIDA );
        purchaseOrderReception.setPurchaseOrder(purchaseOrder);
        PurchaseOrderReception purchaseOrderReceptionUpdated = purchaseOrderReceptionRepository.save( purchaseOrderReception );
        /*"Inicio proceso de recepcion de la orden #"+purchaseOrder.getId()*/
        return DetailedPurchaseOrderReceptionDTO.toDTO( purchaseOrderReceptionUpdated );
    }

    public DetailedPurchaseOrderReceptionDTO checkReviewOrderReception(String id) throws ResourceNotFoundException {
        PurchaseOrderReception purchaseOrderReception = this.findById( id );
        PurchaseOrder purchaseOrder = purchaseOrderService.findById( purchaseOrderReception.getPurchaseOrder().getId() );

        purchaseOrder.setStatus( PurchaseOrderStatus.EN_REVISION );

        purchaseOrderReception.setStatus(PurchaseOrderReceptionStatus.EN_REVISION);
        purchaseOrderReception.setReviewDate( new Timestamp(System.currentTimeMillis()));

        purchaseOrderReception.setPurchaseOrder(purchaseOrder);
        PurchaseOrderReception purchaseOrderReceptionUpdated = purchaseOrderReceptionRepository.save( purchaseOrderReception );

        return DetailedPurchaseOrderReceptionDTO.toDTO( purchaseOrderReceptionUpdated );
    }

    public DetailedPurchaseOrderReceptionDTO acceptOrRejectOrderMaterials(String receptionId, AcceptAndRejectPurchaseOrderDTO acceptAndRejectPurchaseOrderDTO) throws ResourceNotFoundException {
        PurchaseOrderReception purchaseOrderReception = this.findById( receptionId );
        PurchaseOrder purchaseOrder = purchaseOrderService.findById( purchaseOrderReception.getPurchaseOrder().getId() );

        purchaseOrder.setStatus( PurchaseOrderStatus.COMPLETADA );

        purchaseOrderReception.setStatus(PurchaseOrderReceptionStatus.COMPLETADO);
        purchaseOrderReception.setCompletedDate( new Timestamp(System.currentTimeMillis()));

        Grocer grocer = purchaseOrderReception.getGrocer();
        grocer.setStatus( GrocerStatus.DISPONIBLE );

        purchaseOrderReception.setGrocer( grocer );

        PurchaseOrderReception purchaseOrderReceptionUpdated = purchaseOrderReceptionRepository.save( purchaseOrderReception );

        purchaseOrder.setPurchaseOrderReception( purchaseOrderReceptionUpdated );

        List<PurchaseOrderDetail> orderDetailList = purchaseOrder.getPurchaseOrderDetails().stream().map(d -> {
            if(acceptAndRejectPurchaseOrderDTO.acceptedOrderDetailIds().stream().anyMatch(i -> i.equals(d.getId()))){
                d.setStatus( PurchaseOrderDetailStatus.ACEPTADO );
            }else {
                d.setStatus( PurchaseOrderDetailStatus.RECHAZADO );
            }
            return d;
        }).toList();

        List<PurchaseOrderDetail> purchaseOrderDetailListUpdated = purchaseOrderDetailRepository.saveAll( orderDetailList );

        List<PurchaseOrderDetail> rejectedOrderDetails = purchaseOrderDetailListUpdated.stream().filter(o -> o.getStatus().equals(PurchaseOrderDetailStatus.RECHAZADO)).toList();
        if( rejectedOrderDetails.size() > 0 ){
            RejectionGuide newRejectionGuide = RejectionGuide.builder()
                    .reason(acceptAndRejectPurchaseOrderDTO.rejectReason())
                    .suggestions(acceptAndRejectPurchaseOrderDTO.suggestions())
                    .purchaseOrder(purchaseOrder)
                    .productConditions(acceptAndRejectPurchaseOrderDTO.rejectConditions())
                    .grocer(grocer)
                    .date(new Timestamp(System.currentTimeMillis()))
                    .build();

            RejectionGuide rejectionGuideCreated = rejectionGuideRepository.save( newRejectionGuide );
            purchaseOrder.setRejectionGuide( rejectionGuideCreated );
        }
        if (rejectedOrderDetails.size() != purchaseOrderDetailListUpdated.size() ) {
            Warehouse warehouse = warehouseService.findById( acceptAndRejectPurchaseOrderDTO.warehouseLocation() );

            EntryGuide newEntryGuide = EntryGuide.builder()
                    .date(new Timestamp(System.currentTimeMillis()))
                    .purchaseOrder(purchaseOrder)
                    .productConditions(acceptAndRejectPurchaseOrderDTO.acceptConditions())
                    .warehouse(warehouse)
                    .grocer(grocer)
                    .build();

            EntryGuide entryGuideCreated = entryGuideRepository.save( newEntryGuide );

            List<InventoryMovements> newInventoryMovementsList = new ArrayList<>();
            purchaseOrderDetailListUpdated.forEach( pod -> {
                InventoryMovements newInventoryMovements = InventoryMovements.builder()
                        .amount(pod.getAmount())
                        .date(new Timestamp(System.currentTimeMillis()))
                        .type(InventoryMovementType.ENTRADA)
                        .reason("Adquisicion")
                        .warehouse(warehouse)
                        .entryGuide( entryGuideCreated )
                        .build();
                RawMaterial rawMaterial = pod.getRawMaterial();
                if( rawMaterial != null ){
                    int newStock = rawMaterial.getStock() + pod.getAmount();
                    rawMaterial.setStock( newStock );
                    newInventoryMovements.setRawMaterial( rawMaterial );
                    newInventoryMovements.setInitialStock( rawMaterial.getStock() );
                    newInventoryMovements.setNewStock( newStock );

                    //rawMaterialService.save( rawMaterial );
                }else{
                    Product product = pod.getProduct();
                    int newStock = product.getStock() + pod.getAmount();
                    product.setStock( newStock );
                    newInventoryMovements.setProduct( product );
                    newInventoryMovements.setInitialStock( product.getStock() );
                    newInventoryMovements.setNewStock(newStock);

                    //productRepository.save( product );
                }
                newInventoryMovementsList.add( newInventoryMovements );
            });

            inventoryMovementsService.saveAll(newInventoryMovementsList);
            purchaseOrder.setEntryGuide( entryGuideCreated );
        }

        purchaseOrder.setPurchaseOrderDetails(purchaseOrderDetailListUpdated);
        purchaseOrderService.save( purchaseOrder );

        return DetailedPurchaseOrderReceptionDTO.toDTO( purchaseOrderReceptionUpdated );
    }
}
