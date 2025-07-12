package com.furniture.miley.delivery.controller;

import com.furniture.miley.delivery.dto.ShippingLocationDTO;
import com.furniture.miley.security.model.MainUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.sql.Timestamp;

@Slf4j
@Controller
public class DeliveryController {

    @MessageMapping("/location")
    @SendToUser("/queue/delivery")
    public String handle(ShippingLocationDTO shippingLocation, Principal principal) {
        MainUser mainUser = (MainUser) principal;
        shippingLocation.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        log.info("Receive Shipping Location From Carrier {}, Update: {}", mainUser.getEmail(), shippingLocation);
        return shippingLocation.toString();
    }

}
