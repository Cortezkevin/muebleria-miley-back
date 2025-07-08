package com.furniture.miley.sales.controller;

import com.furniture.miley.commons.constants.ResponseMessage;
import com.furniture.miley.commons.dto.SuccessResponseDTO;
import com.furniture.miley.exception.customexception.*;
import com.furniture.miley.sales.dto.order.DetailedOrderDTO;
import com.furniture.miley.sales.dto.order.InvoiceDTO;
import com.furniture.miley.sales.dto.order.OrderDTO;
import com.furniture.miley.sales.dto.order.UpdateDatesDTO;
import com.furniture.miley.sales.dto.order.preparation.*;
import com.furniture.miley.sales.dto.order.shipping.*;
import com.furniture.miley.sales.service.OrderPreparationService;
import com.furniture.miley.sales.service.OrderService;
import com.furniture.miley.sales.service.OrderShippingService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderPreparationService orderPreparationService;
    private final OrderShippingService orderShippingService;

    @PreAuthorize("hasAnyAuthority('ROLE_WAREHOUSE','ROLE_TRANSPORT','ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<SuccessResponseDTO<List<OrderDTO>>> findAll(){
        List<OrderDTO> orderDTOList = orderService.getAll();
        return orderDTOList.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(
                        new SuccessResponseDTO<>(
                                ResponseMessage.SUCCESS,
                                HttpStatus.OK.name(),
                                orderDTOList
                        )
        );
    }

    //@PreAuthorize("hasAnyAuthority('ROLE_WAREHOUSE','ROLE_ADMIN')")
    @GetMapping("/preparation")
    public ResponseEntity<SuccessResponseDTO<List<OrderPreparationDTO>>> findAllPreparationOrders(){
        List<OrderPreparationDTO> orderPreparationDTOList = orderPreparationService.getAll();
        return orderPreparationDTOList.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.SUCCESS,
                        HttpStatus.OK.name(),
                        orderPreparationDTOList
                )
        );
    }

    //@PreAuthorize("hasAnyAuthority('ROLE_TRANSPORT','ROLE_ADMIN')")
    @GetMapping("/shipping")
    public ResponseEntity<SuccessResponseDTO<List<OrderShippingDTO>>> findAllShippingOrders(){
        List<OrderShippingDTO> orderShippingDTOList = orderShippingService.getAll();
        return orderShippingDTOList.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.SUCCESS,
                        HttpStatus.OK.name(),
                        orderShippingDTOList
                )
        );
    }

    @GetMapping("/preparation/{orderPreparationId}")
    public ResponseEntity<SuccessResponseDTO<DetailedPreparationOrder>> findOrderPreparationById(
            @PathVariable String orderPreparationId
    ) throws ResourceNotFoundException {
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.SUCCESS,
                        HttpStatus.OK.name(),
                        orderPreparationService.getDetailsById( orderPreparationId )
                )
        );
    }

    @GetMapping("/shipping/{orderShippingId}")
    public ResponseEntity<SuccessResponseDTO<DetailedShippingOrder>> findOrderShippingById(
            @PathVariable String orderShippingId
    ) throws ResourceNotFoundException {
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.SUCCESS,
                        HttpStatus.OK.name(),
                        orderShippingService.getDetailsById( orderShippingId )
                )
        );
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<SuccessResponseDTO<DetailedOrderDTO>> findById(
            @PathVariable String orderId
    ) throws ResourceNotFoundException {
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.SUCCESS,
                        HttpStatus.OK.name(),
                        orderService.getDetailsById( orderId )
                )
        );
    }

    @GetMapping("/findBy/{userId}")
    public ResponseEntity<SuccessResponseDTO<List<OrderDTO>>> findByUser(
            @PathVariable String userId
    ) throws ResourceNotFoundException {
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.SUCCESS,
                        HttpStatus.OK.name(),
                        orderService.getByUser(userId)
                )
        );
    }

    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<SuccessResponseDTO<OrderDTO>> cancel(
            @PathVariable String orderId
    ) throws CannotCancelOrderException, ResourceNotFoundException {
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.ORDER_CANCELLED,
                        HttpStatus.OK.name(),
                        orderService.cancelOrder( orderId )
                )
        );
    }

    @GetMapping("/invoice")
    public ResponseEntity<Resource> exportInvoice(
            @RequestParam("order") String orderId
    ){
        InvoiceDTO invoiceDTO = orderService.exportInvoice( orderId);
        return ResponseEntity.ok()
                .contentLength(invoiceDTO.invoiceLength().longValue())
                .contentType(MediaType.APPLICATION_PDF)
                .headers(invoiceDTO.headers())
                .body(new ByteArrayResource(invoiceDTO.resource()));
    }

    //@PreAuthorize("hasAnyAuthority('ROLE_TRANSPORT','ROLE_ADMIN')")
    @PostMapping("/shipping/start")
    public ResponseEntity<SuccessResponseDTO<OrderShippingDTO>> startShippingOrder(
            @RequestBody StartOrderShippingDTO startOrderShippingDTO
    ) throws AbortedProcessException, AlreadyStartedProcessException, FinishCurrentProcessException, ResourceNotFoundException {
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.STARTED_SHIPPING_ORDER,
                        HttpStatus.OK.name(),
                        orderShippingService.startShippingOrder( startOrderShippingDTO )
                )
        );
    }


    //@PreAuthorize("hasAnyAuthority('ROLE_TRANSPORT','ROLE_ADMIN')")
    @PostMapping("/shipping/prepare")
    public ResponseEntity<SuccessResponseDTO<OrderShippingDTO>> checkPrepareShippingOrder(
            @RequestBody PreparedOrderShippingDTO preparedOrderShippingDTO
    ) throws AbortedProcessException, ResourceNotFoundException {
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.PREPARED_SHIPPING_ORDER,
                        HttpStatus.OK.name(),
                        orderShippingService.checkOrderShippingPrepared( preparedOrderShippingDTO )
                )
        );
    }


    //@PreAuthorize("hasAnyAuthority('ROLE_TRANSPORT','ROLE_ADMIN')")
    @PostMapping("/shipping/transit")
    public ResponseEntity<SuccessResponseDTO<OrderShippingDTO>> checkTransitShippingOrder(
            @RequestBody TransitOrderShippingDTO transitOrderShippingDTO
    ) throws AbortedProcessException, ResourceNotFoundException {
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.IN_TRANSIT_SHIPPING_ORDER,
                        HttpStatus.OK.name(),
                        orderShippingService.checkOrderShippingTransit( transitOrderShippingDTO )
                )
        );
    }

    //@PreAuthorize("hasAnyAuthority('ROLE_TRANSPORT','ROLE_ADMIN')")
    @PostMapping("/shipping/complete")
    public ResponseEntity<SuccessResponseDTO<OrderShippingDTO>> completeShippingOrder(
            @RequestBody CompleteOrderShippingDTO completeOrderShippingDTO
    ) throws AbortedProcessException, ResourceNotFoundException {
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.COMPLETED_SHIPPING_ORDER,
                        HttpStatus.OK.name(),
                        orderShippingService.checkOrderShippingComplete( completeOrderShippingDTO )
                )
        );
    }

    //@PreAuthorize("hasAnyAuthority('ROLE_WAREHOUSE','ROLE_ADMIN')")
    @PostMapping("/preparation/start")
    public ResponseEntity<SuccessResponseDTO<OrderPreparationDTO>> startPreparationOrder(
            @RequestBody StartOrderPreparationDTO startOrderPreparationDTO
    ) throws AbortedProcessException, AlreadyStartedProcessException, FinishCurrentProcessException, ResourceNotFoundException {
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.STARTED_PREPARATION_ORDER,
                        HttpStatus.OK.name(),
                        orderPreparationService.startPreparationOrder( startOrderPreparationDTO )
                )
        );
    }


    //@PreAuthorize("hasAnyAuthority('ROLE_WAREHOUSE','ROLE_ADMIN')")
    @PostMapping("/preparation/packaging")
    public ResponseEntity<SuccessResponseDTO<OrderPreparationDTO>> checkPackagingPreparationOrder(
            @RequestBody PackagingOrderPreparationDTO packagingOrderPreparationDTO
    ) throws AbortedProcessException, ResourceNotFoundException {
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.PACKAGE_PREPARATION_ORDER,
                        HttpStatus.OK.name(),
                        orderPreparationService.checkOrderPreparationPackaging( packagingOrderPreparationDTO )
                )
        );
    }


    //@PreAuthorize("hasAnyAuthority('ROLE_WAREHOUSE','ROLE_ADMIN')")
    @PostMapping("/preparation/complete")
    public ResponseEntity<SuccessResponseDTO<OrderPreparationDTO>> completePreparationOrder(
            @RequestBody CompletedOrderPreparationDTO completedOrderPreparationDTO
    ) throws AbortedProcessException, ResourceNotFoundException {
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.COMPLETED_PREPARATION_ORDER,
                        HttpStatus.OK.name(),
                        orderPreparationService.checkOrderPreparationCompleted( completedOrderPreparationDTO )
                )
        );
    }

    @PostMapping("/dates")
    public ResponseEntity<String> updateDates(
            @RequestBody UpdateDatesDTO updateDates
    ){
        return ResponseEntity.ok(orderService.updateDates(updateDates) );
    }
}
