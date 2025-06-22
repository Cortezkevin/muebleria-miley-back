package com.furniture.miley.sales.model.order;

import com.furniture.miley.sales.enums.PreparationStatus;
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
public class OrderPreparation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private Timestamp createdDate;
    private Timestamp startDate;
    private Timestamp preparedDate;
    private Timestamp completedDate;

    @Enumerated( EnumType.STRING )
    private PreparationStatus status;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Grocer grocer;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Order order;
}
