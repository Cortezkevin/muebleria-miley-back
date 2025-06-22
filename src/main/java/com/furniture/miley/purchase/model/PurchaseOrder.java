package com.furniture.miley.purchase.model;

import com.furniture.miley.purchase.enums.PurchaseOrderStatus;
import com.furniture.miley.security.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private Timestamp date;

    @Enumerated(EnumType.STRING)
    private PurchaseOrderStatus status;

    private BigDecimal total;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Supplier supplier;

    @OneToOne(mappedBy = "purchaseOrder", cascade = CascadeType.ALL)
    private PurchaseOrderReception purchaseOrderReception;

    @OneToOne(mappedBy = "purchaseOrder")
    private EntryGuide entryGuide;

    @OneToOne(mappedBy = "purchaseOrder")
    private RejectionGuide rejectionGuide;

    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL)
    private List<PurchaseOrderDetail> purchaseOrderDetails = new ArrayList<>();
}
