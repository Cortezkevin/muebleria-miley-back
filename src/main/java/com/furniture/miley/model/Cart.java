package com.furniture.miley.model;

import com.furniture.miley.security.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private BigDecimal tax = BigDecimal.ZERO;
    private BigDecimal discount = BigDecimal.ZERO;
    private BigDecimal subtotal = BigDecimal.ZERO;
    private BigDecimal shippingCost = BigDecimal.ZERO;
    private BigDecimal total = BigDecimal.ZERO;
    @OneToMany(mappedBy = "cart", fetch = FetchType.LAZY)
    private List<CartItem> cartItems = new ArrayList<>();
    @OneToOne(fetch = FetchType.EAGER)
    private User user;

    public void calculateTotals() {
        if (cartItems != null && cartItems.size() > 0) {
            BigDecimal newTotal = BigDecimal.ZERO;
            BigDecimal newSubtotal = BigDecimal.ZERO;
            for (CartItem c : this.cartItems) {
                newTotal = newTotal.add(c.getTotal());
                newSubtotal = newSubtotal.add(c.getTotal());
            }
            newTotal = newTotal.add(shippingCost);
            this.total = newTotal;
            this.subtotal = newSubtotal;
        }
    }

    public static Cart createEmpty() {
        return Cart.builder()
                .subtotal(BigDecimal.ZERO)
                .total(BigDecimal.ZERO)
                .shippingCost(BigDecimal.ZERO)
                .tax(BigDecimal.ZERO)
                .discount(BigDecimal.ZERO)
                .build();
    }
}
