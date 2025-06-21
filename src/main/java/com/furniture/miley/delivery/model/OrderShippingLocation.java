package com.furniture.miley.delivery.model;

import com.furniture.miley.sales.model.order.OrderShipping;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderShippingLocation {
    @Id
    @GeneratedValue( strategy = GenerationType.UUID)
    private String id;
    private Double lta;
    private Double lng;
    private Timestamp updatedAt;

    @ManyToOne(optional = false)
    private Carrier carrier;

    @OneToOne(mappedBy = "shippingLocation")
    private OrderShipping orderShipping;

}
