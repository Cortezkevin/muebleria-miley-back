package com.furniture.miley.delivery.controller;

import com.furniture.miley.commons.constants.ResponseMessage;
import com.furniture.miley.commons.dto.SuccessResponseDTO;
import com.furniture.miley.exception.customexception.ResourceNotFoundException;
import com.furniture.miley.sales.dto.order.DetailedOrderDTO;
import com.furniture.miley.sales.dto.order.OrderDTO;
import com.furniture.miley.sales.dto.order.shipping.OrderShippingDTO;
import com.furniture.miley.sales.service.OrderService;
import com.furniture.miley.sales.service.OrderShippingService;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/shipping-location")
@RequiredArgsConstructor
public class ShippingLocationController {

    private final OrderShippingService orderShippingService;

    @GetMapping("/test-notification/{title}")
    public String sendTestNotification(
            @PathVariable String title
    ) throws ResourceNotFoundException, FirebaseMessagingException {
        orderShippingService.sendTestNotification(title);
        return "Notificacion enviada";
    }

    @GetMapping
    public ResponseEntity<SuccessResponseDTO<List<DetailedOrderDTO>>> getAllReadyToSend() throws ResourceNotFoundException {
        List<DetailedOrderDTO> orderShippingDTOList = orderShippingService.getAllReadyToSend();
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

    @GetMapping("/confirm-reception/{orderId}")
    public ResponseEntity<SuccessResponseDTO<String>> confirmOrderCompleted(
            @PathVariable("orderId") String orderId
    ) throws ResourceNotFoundException {
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.CONFIRMED_ORDER_RECEPTION,
                        HttpStatus.OK.name(),
                        orderShippingService.confirmOrderReception(orderId)
                )
        );
    }
}
