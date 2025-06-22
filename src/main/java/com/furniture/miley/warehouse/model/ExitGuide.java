package com.furniture.miley.warehouse.model;

import com.furniture.miley.sales.model.order.Order;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExitGuide {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private Timestamp date;
    private String observations;

    @ManyToOne(fetch = FetchType.EAGER)
    private Grocer grocer;

    @OneToOne(fetch = FetchType.EAGER)
    private Order order;

    @ManyToOne(fetch = FetchType.EAGER)
    private Warehouse warehouse;

    /*@OneToOne(fetch = FetchType.EAGER)
    private FabricationProduct fabricationProduct;*/

    @OneToMany(mappedBy = "exitGuide")
    private List<InventoryMovements> inventoryMovementsList = new ArrayList<>();
}
