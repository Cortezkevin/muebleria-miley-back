package com.furniture.miley.delivery.controller;

import com.furniture.miley.delivery.dto.ShippingLocationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.sql.Timestamp;

@Slf4j
@Controller
public class DeliveryController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/location")
    @SendTo("/topic/delivery")
    public ShippingLocationDTO handle(ShippingLocationDTO shippingLocation) {
        log.info("Receive Shipping Location From Carrier {}, Update: {}", shippingLocation.getCarrier(), shippingLocation);

        String receptor = shippingLocation.getClientReceiver();
        shippingLocation.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        log.info("Ubicación recibida de: {}, enviada a: {}", shippingLocation.getCarrier(), receptor);

        return shippingLocation;
    }

}
