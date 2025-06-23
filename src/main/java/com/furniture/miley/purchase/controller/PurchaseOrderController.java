package com.furniture.miley.purchase.controller;

import com.furniture.miley.commons.constants.ResponseMessage;
import com.furniture.miley.commons.dto.SuccessResponseDTO;
import com.furniture.miley.exception.customexception.ResourceNotFoundException;
import com.furniture.miley.purchase.dto.purchaseOrder.CreatePurchaseOrderDTO;
import com.furniture.miley.purchase.dto.purchaseOrder.DetailedPurchaseOrderDTO;
import com.furniture.miley.purchase.dto.purchaseOrder.PurchaseOrderDTO;
import com.furniture.miley.purchase.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/purchase_order")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @GetMapping
    public ResponseEntity<SuccessResponseDTO<List<PurchaseOrderDTO>>> getAll(){
        List<PurchaseOrderDTO> purchaseOrderDTOList = purchaseOrderService.getAll();
        return purchaseOrderDTOList.isEmpty()
                    ? ResponseEntity.noContent().build()
                    : ResponseEntity.ok(
                            new SuccessResponseDTO<>(
                                    ResponseMessage.SUCCESS,
                                    HttpStatus.OK.name(),
                                    purchaseOrderDTOList
                            )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponseDTO<DetailedPurchaseOrderDTO>> getById(
            @PathVariable String id
    ) throws ResourceNotFoundException {
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.SUCCESS,
                        HttpStatus.OK.name(),
                        purchaseOrderService.getDetailsById( id )
                )
        );
    }

    @PutMapping("/cancel/{id}")
    public ResponseEntity<SuccessResponseDTO<DetailedPurchaseOrderDTO>> cancelPurchaseOrder(
            @PathVariable String id
    ) throws ResourceNotFoundException {
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.PURCHASE_ORDER_CANCELLED,
                        HttpStatus.OK.name(),
                        purchaseOrderService.cancelPurchaseOrder( id )
                )
        );
    }

    @PostMapping
    public ResponseEntity<SuccessResponseDTO<PurchaseOrderDTO>> create(
            @RequestBody CreatePurchaseOrderDTO createPurchaseOrderDTO
            ) throws ResourceNotFoundException {
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.PURCHASE_ORDER_CREATED,
                        HttpStatus.OK.name(),
                        purchaseOrderService.create( createPurchaseOrderDTO )
                )
        );
    }
}
