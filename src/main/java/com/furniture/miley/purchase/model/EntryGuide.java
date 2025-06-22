package com.furniture.miley.purchase.model;

import com.furniture.miley.warehouse.model.Grocer;
import com.furniture.miley.warehouse.model.InventoryMovements;
import com.furniture.miley.warehouse.model.Warehouse;
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
public class EntryGuide {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private Timestamp date;
    private String productConditions;

    @ManyToOne(fetch = FetchType.EAGER)
    private Grocer grocer;

    @OneToOne(fetch = FetchType.EAGER)
    private PurchaseOrder purchaseOrder;

    @ManyToOne(fetch = FetchType.EAGER)
    private Warehouse warehouse;

    @OneToMany(mappedBy = "entryGuide")
    private List<InventoryMovements> inventoryMovementsList = new ArrayList<>();

}
