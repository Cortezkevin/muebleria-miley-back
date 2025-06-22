package com.furniture.miley.purchase.model;

import com.furniture.miley.purchase.enums.MeasurementUnit;
import com.furniture.miley.warehouse.model.InventoryMovements;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RawMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;
    private String description;

    @Enumerated(EnumType.STRING)
    private MeasurementUnit measurementUnit;

    private BigDecimal unitPrice = BigDecimal.ZERO;
    private Integer stock = 0;

    @OneToMany(mappedBy = "rawMaterial", fetch = FetchType.LAZY)
    private List<PurchaseOrderDetail> purchaseOrderDetails = new ArrayList<>();

    @OneToMany(mappedBy = "rawMaterial", fetch = FetchType.LAZY)
    private List<InventoryMovements> inventoryMovements = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    private Supplier supplier;

    /*@OneToMany(mappedBy = "rawMaterial", fetch = FetchType.LAZY)
    private List<ProductMaterials> productRawMaterials = new ArrayList<>();*/
}
