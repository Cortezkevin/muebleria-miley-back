package com.furniture.miley.warehouse.model;

import com.furniture.miley.purchase.model.PurchaseOrderReception;
import com.furniture.miley.sales.model.order.OrderPreparation;
import com.furniture.miley.security.model.User;
import com.furniture.miley.warehouse.enums.GrocerStatus;
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
public class Grocer {
    @Id
    @GeneratedValue( strategy = GenerationType.UUID)
    private String id;

    @Enumerated( EnumType.STRING )
    private GrocerStatus status;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private User user;

    @OneToMany(mappedBy = "grocer", fetch = FetchType.LAZY)
    @Builder.Default
    private List<OrderPreparation> orderPreparations = new ArrayList<>();

    @OneToMany(mappedBy = "grocer", fetch = FetchType.LAZY)
    private List<PurchaseOrderReception> purchaseOrderReceptions = new ArrayList<>();
}
