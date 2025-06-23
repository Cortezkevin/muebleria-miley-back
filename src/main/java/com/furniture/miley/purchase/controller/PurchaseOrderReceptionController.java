package com.furniture.miley.purchase.controller;

import com.furniture.miley.commons.constants.ResponseMessage;
import com.furniture.miley.commons.dto.SuccessResponseDTO;
import com.furniture.miley.exception.customexception.AbortedProcessException;
import com.furniture.miley.exception.customexception.ResourceNotFoundException;
import com.furniture.miley.purchase.dto.purchaseOrder.AcceptAndRejectPurchaseOrderDTO;
import com.furniture.miley.purchase.dto.purchaseOrder.DetailedPurchaseOrderReceptionDTO;
import com.furniture.miley.purchase.dto.purchaseOrder.PurchaseOrderDTO;
import com.furniture.miley.purchase.dto.purchaseOrder.PurchaseOrderReceptionDTO;
import com.furniture.miley.purchase.service.PurchaseOrderReceptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/purchase_order_reception")
@RequiredArgsConstructor
public class PurchaseOrderReceptionController {

    private final PurchaseOrderReceptionService purchaseOrderReceptionService;

    @GetMapping
    public ResponseEntity<SuccessResponseDTO<List<PurchaseOrderReceptionDTO>>> getAll(){
        List<PurchaseOrderReceptionDTO> purchaseOrderReceptionDTOList = purchaseOrderReceptionService.getAll();
        return purchaseOrderReceptionDTOList.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.SUCCESS,
                        HttpStatus.OK.name(),
                        purchaseOrderReceptionDTOList
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponseDTO<DetailedPurchaseOrderReceptionDTO>> getById(
            @PathVariable String id
    ) throws ResourceNotFoundException {
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.SUCCESS,
                        HttpStatus.OK.name(),
                        purchaseOrderReceptionService.getDetailsById( id )
                )
        );
    }

    @PostMapping("/{id}")
    public ResponseEntity<SuccessResponseDTO<DetailedPurchaseOrderReceptionDTO>> startOrderReception(
            @PathVariable String id,
            @RequestParam(name = "purchaseOrderId") String purchaseOrderId,
            @RequestParam(name = "grocerId") String grocerId
    ) throws AbortedProcessException, ResourceNotFoundException {
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.STARTED_RECEPTION_ORDER,
                        HttpStatus.OK.name(),
                        purchaseOrderReceptionService.startOrderReception( purchaseOrderId, id, grocerId )
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponseDTO<DetailedPurchaseOrderReceptionDTO>> checkReviewOrderReception(
            @PathVariable String id
    ) throws ResourceNotFoundException {
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.REVIEW_RECEPTION_ORDER,
                        HttpStatus.OK.name(),
                        purchaseOrderReceptionService.checkReviewOrderReception( id )
                )
        );
    }

    @PutMapping("/acceptOrReject/{id}")
    public ResponseEntity<SuccessResponseDTO<DetailedPurchaseOrderReceptionDTO>> acceptOrRejectOrderMaterials(
            @PathVariable String id,
            @RequestBody AcceptAndRejectPurchaseOrderDTO acceptAndRejectPurchaseOrderDTO
            ) throws ResourceNotFoundException {
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.SUCCESS,
                        HttpStatus.OK.name(),
                        purchaseOrderReceptionService.acceptOrRejectOrderMaterials( id, acceptAndRejectPurchaseOrderDTO )
                )
        );
    }
}
