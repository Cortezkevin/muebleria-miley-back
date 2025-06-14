package com.furniture.miley.sales.dto.cart;

import com.furniture.miley.sales.model.Cart;

import java.math.BigDecimal;
import java.util.List;

public record CartDTO(
        String id,
        BigDecimal tax,
        BigDecimal discount,
        BigDecimal subtotal,
        BigDecimal shippingCost,
        Double distance,
        BigDecimal total,
        List<CartItemDTO> cartItems,
        String user_id
) {
    public static CartDTO fromEntity(Cart cart){
        return new CartDTO(
                cart.getId(),
                cart.getTax(),
                cart.getDiscount(),
                cart.getSubtotal(),
                cart.getShippingCost(),
                cart.getDistance(),
                cart.getTotal(),
                cart.getCartItems().stream().map( CartItemDTO::fromEntity ).toList(),
                cart.getUser().getId()
        );
    }
}
