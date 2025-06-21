package com.furniture.miley.sales.model.order;

import com.furniture.miley.catalog.model.Product;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
    private BigDecimal price;
    private Integer amount;
    private BigDecimal total;
    @ManyToOne(fetch = FetchType.EAGER)
    private Order order;
    @ManyToOne(fetch = FetchType.EAGER)
    private Product product;
}
