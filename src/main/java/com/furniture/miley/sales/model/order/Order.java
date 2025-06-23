package com.furniture.miley.sales.model.order;

import com.furniture.miley.sales.enums.OrderStatus;
import com.furniture.miley.sales.enums.PaymentMethod;
import com.furniture.miley.security.model.User;
import com.furniture.miley.warehouse.model.ExitGuide;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Table(name = "orders")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String note;
    private String shippingAddress;
    private String specificAddress;
    private BigDecimal shippingCost;
    private Double distance = 0.0;
    private BigDecimal tax;
    private BigDecimal discount;
    private BigDecimal subtotal;
    private BigDecimal total;

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    @OneToMany(mappedBy = "order")
    private List<OrderDetail> orderDetails = new ArrayList<>();

    private Timestamp createdDate;
    private Timestamp cancelledDate;
    private Timestamp completedDate;

    @Enumerated( EnumType.STRING )
    private PaymentMethod paymentMethod;

    @OneToOne(mappedBy = "order")
    private OrderShipping orderShipping;

    @OneToOne(mappedBy = "order")
    private OrderPreparation orderPreparation;

    @Enumerated( EnumType.STRING )
    private OrderStatus status;

    @OneToOne(mappedBy = "order")
    private Invoice invoice;

    @OneToOne(mappedBy = "order")
    private ExitGuide exitGuide;
}
