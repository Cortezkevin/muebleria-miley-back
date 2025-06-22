package com.furniture.miley.warehouse.model;

import com.furniture.miley.catalog.model.Product;
import com.furniture.miley.purchase.model.EntryGuide;
import com.furniture.miley.purchase.model.RawMaterial;
import com.furniture.miley.warehouse.enums.InventoryMovementType;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InventoryMovements {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Enumerated(EnumType.STRING)
    private InventoryMovementType type;

    private Integer initialStock;
    private Integer amount;
    private Integer newStock;
    private Timestamp date;
    private String reason;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Product product;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private RawMaterial rawMaterial;

    @ManyToOne(fetch = FetchType.EAGER)
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.EAGER)
    private EntryGuide entryGuide;

    @ManyToOne(fetch = FetchType.EAGER)
    private ExitGuide exitGuide;
}
