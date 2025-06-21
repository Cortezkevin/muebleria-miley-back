package com.furniture.miley.sales.model.order;

import com.furniture.miley.delivery.model.Carrier;
import com.furniture.miley.delivery.model.OrderShippingLocation;
import com.furniture.miley.sales.enums.ShippingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderShipping {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String address;
    private String preparedBy;
    private Double distance = 0.0;
    private Double destinationLat;
    private Double destinationLng;
    private Timestamp createdDate;
    private Timestamp startDate;
    private Timestamp preparedDate;
    private Timestamp shippingDate;
    private Timestamp completedDate;

    @OneToOne(fetch = FetchType.LAZY)
    private OrderShippingLocation shippingLocation;

    @ManyToOne(fetch = FetchType.EAGER)
    private Carrier carrier;

    @Enumerated( EnumType.STRING )
    private ShippingStatus status;

    @OneToOne(fetch = FetchType.EAGER)
    private Order order;
}
