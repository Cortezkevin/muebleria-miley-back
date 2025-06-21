package com.furniture.miley.sales.model.cart;

import com.furniture.miley.security.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    @Id
    @GeneratedValue( strategy = GenerationType.UUID )
    private String id;
    private BigDecimal tax = BigDecimal.ZERO;
    private BigDecimal discount = BigDecimal.ZERO;
    private BigDecimal subtotal = BigDecimal.ZERO;
    private BigDecimal shippingCost = BigDecimal.ZERO;
    private Double distance = 0.0;
    private BigDecimal total = BigDecimal.ZERO;

    @OneToMany(mappedBy = "cart", fetch = FetchType.LAZY)
    private List<CartItem> cartItems = new ArrayList<>();

    @OneToOne(fetch = FetchType.EAGER)
    private User user;

    public void calculateTotals(){
        if (cartItems != null && !cartItems.isEmpty()) {
            BigDecimal newTotal = BigDecimal.ZERO;
            BigDecimal newSubtotal = BigDecimal.ZERO;
            BigDecimal newTax = BigDecimal.ZERO;
            for (CartItem c: this.cartItems){
                newTotal = newTotal.add( c.getTotal() );
                newSubtotal = newSubtotal.add( c.getTotal() );
            }
            if( subtotal.floatValue() < 2500.00){
                newTotal = newTotal.add( shippingCost );
            }
            newTax = newTotal.multiply( BigDecimal.valueOf(0.18) ).setScale(2, RoundingMode.HALF_UP);
            newTotal = newTotal.add( newTax );
            this.tax = newTax;
            this.total = newTotal.setScale(2, RoundingMode.HALF_UP);
            this.subtotal = newSubtotal.setScale(2, RoundingMode.HALF_UP);
        }else {
            this.subtotal = BigDecimal.ZERO;
            this.tax = this.shippingCost.multiply( BigDecimal.valueOf(0.18) ).setScale(2, RoundingMode.HALF_UP);
            this.total = this.tax.add(this.shippingCost);
        }
    }

    public static Cart createEmpty(){
        return Cart.builder()
                .subtotal(BigDecimal.ZERO)
                .total(BigDecimal.ZERO)
                .shippingCost(BigDecimal.ZERO)
                .tax(BigDecimal.ZERO)
                .discount(BigDecimal.ZERO)
                .build();
    }
}