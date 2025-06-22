package com.furniture.miley.purchase.service;

import com.furniture.miley.catalog.model.Product;
import com.furniture.miley.catalog.service.ProductService;
import com.furniture.miley.exception.customexception.ResourceNotFoundException;
import com.furniture.miley.purchase.dto.purchaseOrder.CreatePurchaseOrderDTO;
import com.furniture.miley.purchase.dto.purchaseOrder.DetailedPurchaseOrderDTO;
import com.furniture.miley.purchase.dto.purchaseOrder.PurchaseOrderDTO;
import com.furniture.miley.purchase.enums.PurchaseOrderDetailStatus;
import com.furniture.miley.purchase.enums.PurchaseOrderReceptionStatus;
import com.furniture.miley.purchase.enums.PurchaseOrderStatus;
import com.furniture.miley.purchase.model.*;
import com.furniture.miley.purchase.repository.*;
import com.furniture.miley.security.enums.RolName;
import com.furniture.miley.security.model.User;
import com.furniture.miley.security.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseOrderService {
    private final PurchaseOrderRepository purchaseOrderRepository;

    private final UserService userService;

    private final PurchaseOrderReceptionRepository purchaseOrderReceptionRepository;
    private final SupplierService supplierService;
    private final PurchaseOrderDetailRepository purchaseOrderDetailRepository;
    private final ProductService productService;
    private final RawMaterialService rawMaterialService;

    public PurchaseOrder save(PurchaseOrder purchaseOrder) {
        return purchaseOrderRepository.save(purchaseOrder);
    }

    public PurchaseOrder findById(String id) throws ResourceNotFoundException {
        return purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden de compra no encontrado"));
    }

    public List<PurchaseOrderDTO> getAll(){
        return purchaseOrderRepository.findAll().stream().map(PurchaseOrderDTO::toDTO).toList();
    }

    public DetailedPurchaseOrderDTO getDetailsById(String id) throws ResourceNotFoundException {
        return DetailedPurchaseOrderDTO.toDTO( this.findById( id ) );
    }

    public DetailedPurchaseOrderDTO cancelPurchaseOrder(String id) throws ResourceNotFoundException {
        PurchaseOrder purchaseOrder = this.findById( id );
        purchaseOrder.setStatus( PurchaseOrderStatus.CANCELADA );

        PurchaseOrderReception purchaseOrderReception = purchaseOrder.getPurchaseOrderReception();
        purchaseOrderReception.setStatus( PurchaseOrderReceptionStatus.CANCELADO );
        //PurchaseOrderReception purchaseOrderReceptionUpdated = purchaseOrderReceptionRepository.save( purchaseOrderReception );
        purchaseOrder.setPurchaseOrderReception( purchaseOrderReception );

        return DetailedPurchaseOrderDTO.toDTO(
                purchaseOrderRepository.save( purchaseOrder )
        );
    }

    public PurchaseOrderDTO create(CreatePurchaseOrderDTO createPurchaseOrderDTO) throws ResourceNotFoundException {
        User user = userService.findById( createPurchaseOrderDTO.userId() );
        if(user.getRoles().stream().noneMatch(r -> r.getRolName().equals(RolName.ROLE_ADMIN))){
            throw new RuntimeException("No tiene permisos para iniciar una orden de compra");
        }

        Supplier supplier = supplierService.findById(createPurchaseOrderDTO.supplierId() );

        PurchaseOrder newPurchaseOrder = PurchaseOrder.builder()
                .date( new Timestamp(new Date().getTime()) )
                .supplier( supplier )
                .status(PurchaseOrderStatus.PENDIENTE)
                .user( user )
                .build();

        PurchaseOrder purchaseOrderCreated = purchaseOrderRepository.save( newPurchaseOrder );
        PurchaseOrderReception newPurchaseOrderReception = PurchaseOrderReception.builder()
                .createdDate( new Timestamp(new Date().getTime()) )
                .purchaseOrder( purchaseOrderCreated )
                .status(PurchaseOrderReceptionStatus.PENDIENTE)
                .build();

        PurchaseOrderReception purchaseOrderReceptionCreated = purchaseOrderReceptionRepository.save( newPurchaseOrderReception );

        purchaseOrderCreated.setPurchaseOrderReception( purchaseOrderReceptionCreated );
        List<PurchaseOrderDetail> purchaseOrderDetails = new ArrayList<>();

        createPurchaseOrderDTO.details().forEach( d -> {
            BigDecimal itemTotal = d.unitPrice().multiply(BigDecimal.valueOf(d.amount()));
            PurchaseOrderDetail purchaseOrderDetail = PurchaseOrderDetail.builder()
                    .amount(d.amount())
                    .unitPrice(d.unitPrice())
                    .total(itemTotal)
                    .purchaseOrder( purchaseOrderCreated )
                    .status(PurchaseOrderDetailStatus.NO_RECEPCIONADO)
                    .build();

            try {
                Product product = productService.findById(d.materialOrProductId());
                purchaseOrderDetail.setProduct(product);
            } catch (ResourceNotFoundException e1) {
                try {
                    RawMaterial rawMaterial = rawMaterialService.findById(d.materialOrProductId());
                    purchaseOrderDetail.setRawMaterial(rawMaterial);
                } catch (ResourceNotFoundException e2) {
                    throw new RuntimeException("Neither Product nor RawMaterial found for ID: " + d.materialOrProductId());
                }
            }

            purchaseOrderDetails.add( purchaseOrderDetail );
        });

        List<PurchaseOrderDetail> purchaseOrderDetailsCreated = purchaseOrderDetailRepository.saveAll( purchaseOrderDetails );
        purchaseOrderCreated.setPurchaseOrderDetails( purchaseOrderDetailsCreated );

        BigDecimal total = BigDecimal.ZERO;
        for(PurchaseOrderDetail p: purchaseOrderDetailsCreated){
            total = total.add( p.getTotal() );
        }

        purchaseOrderCreated.setTotal(total);

        return PurchaseOrderDTO.toDTO(
                purchaseOrderRepository.save( purchaseOrderCreated )
        );
    }
}
