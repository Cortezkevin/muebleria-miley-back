package com.furniture.miley.delivery.model;

import com.furniture.miley.delivery.enums.CarrierStatus;
import com.furniture.miley.sales.model.order.OrderShipping;
import com.furniture.miley.security.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Carrier {
    @Id
    @GeneratedValue( strategy = GenerationType.UUID)
    private String id;
    private String codePlate;

    @Enumerated( EnumType.STRING )
    private CarrierStatus status;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private User user;

    @OneToMany(mappedBy = "carrier", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderShippingLocation> orderShippingLocations = new ArrayList<>();

    @OneToMany(mappedBy = "carrier")
    private List<OrderShipping> orderShipping = new ArrayList<>();
}
