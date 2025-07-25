package com.furniture.miley.delivery.dto;

import lombok.*;

import java.sql.Timestamp;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ShippingLocationDTO {
    private Double lta;
    private Double lng;
    private Timestamp updatedAt;
    private String carrier;
    private String orderId;
    private String clientReceiver;
}
