package com.furniture.miley.catalog.model;

import com.furniture.miley.catalog.enums.AcquisitionType;
import com.furniture.miley.catalog.model.color.ProductColor;
import com.furniture.miley.catalog.model.feature.ProductFeature;
import com.furniture.miley.catalog.model.image.DefaultProductImage;
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
public class Product {
    @Id
    @GeneratedValue( strategy = GenerationType.UUID)
    private String id;

    private String name;
    private String description;
    private BigDecimal price;

    @Enumerated( EnumType.STRING )
    private AcquisitionType acquisitionType;

    @ManyToOne(fetch = FetchType.EAGER)
    private SubCategory subCategory;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductFeature> features = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductColor> colors = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DefaultProductImage> images = new ArrayList<>();
    /*
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<CartItem> cartItems = new ArrayList<>();

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<OrderDetail> orderDetails = new ArrayList<>();

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<PurchaseOrderDetail> purchaseOrderDetails = new ArrayList<>();

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<InventoryMovements> inventoryMovements = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    private Supplier supplier;*/
}
