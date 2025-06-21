package com.furniture.miley.sales.model.cart;

import com.furniture.miley.catalog.model.Product;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private Integer amount;
    private BigDecimal total;
    @ManyToOne(fetch = FetchType.EAGER)
    private Cart cart;
    @ManyToOne(fetch = FetchType.EAGER)
    private Product product;
}
