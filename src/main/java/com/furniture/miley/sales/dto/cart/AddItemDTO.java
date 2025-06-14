package com.furniture.miley.sales.dto.cart;

public record AddItemDTO(
        String cart_id,
        String product_id,
        Integer amount
) {

}
