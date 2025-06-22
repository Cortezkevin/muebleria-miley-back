package com.furniture.miley.purchase.model;
import com.furniture.miley.purchase.enums.PurchaseOrderReceptionStatus;
import com.furniture.miley.warehouse.model.Grocer;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrderReception {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String observations;
    private Timestamp createdDate;
    private Timestamp startDate;
    private Timestamp reviewDate;
    private Timestamp completedDate;

    @Enumerated(EnumType.STRING)
    private PurchaseOrderReceptionStatus status;

    @ManyToOne(fetch = FetchType.EAGER)
    private Grocer grocer;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private PurchaseOrder purchaseOrder;
}
