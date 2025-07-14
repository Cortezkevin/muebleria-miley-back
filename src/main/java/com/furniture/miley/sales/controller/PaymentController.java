package com.furniture.miley.sales.controller;

import com.furniture.miley.commons.constants.ResponseMessage;
import com.furniture.miley.commons.dto.SuccessResponseDTO;
import com.furniture.miley.exception.customexception.ResourceNotFoundException;
import com.furniture.miley.sales.dto.payment.PaymentIndentResponseDTO;
import com.furniture.miley.sales.service.CartService;
import com.furniture.miley.sales.service.PaymentService;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    @Value("${front.url}")
    private String FRONT_PATH;

    private final PaymentService paymentService;
    private final CartService cartService;

    @GetMapping("/success")
    public ResponseEntity<?> successPayment(
            @RequestParam("user") String userId,
            @RequestParam(name = "note", required = false, defaultValue = "No agrego comentarios adicionales") String note,
            @RequestParam(name = "specificAddress", required = false, defaultValue = "No se especifico la direccion") String specificAddress
    ) throws ResourceNotFoundException, FirebaseMessagingException {
        String response = paymentService.successPayment( userId, note, specificAddress );
        cartService.clearCartByUser(userId);

        /* https://creaciones-joaquin-front.vercel.app/ */
        /* http://localhost:3000/ */
        //String externalUrl = "https://creaciones-joaquin-front.vercel.app/cart/checkout/completion";
        String externalUrl = FRONT_PATH + "/cart/checkout/completion";
        return ResponseEntity.status(302).location(ServletUriComponentsBuilder.fromUriString(externalUrl).build().toUri()).build();
    }

    @GetMapping("/cancelIntent/{intentId}")
    public ResponseEntity<SuccessResponseDTO<String>> cancelPaymentIntent(
            @PathVariable String intentId,
            @RequestParam("reason") String reason
    ) throws StripeException, ResourceNotFoundException {
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.SUCCESS,
                        HttpStatus.OK.name(),
                        paymentService.cancelIntent( intentId, reason )
                )
        );
    }

    @PostMapping("/createIndent")
    public ResponseEntity<SuccessResponseDTO<PaymentIndentResponseDTO>> createPaymentIndent(
            @RequestParam("user") String userId
    ) throws StripeException, ResourceNotFoundException {
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.SUCCESS,
                        HttpStatus.OK.name(),
                        paymentService.createIndent( userId )
                )
        );
    }

}
