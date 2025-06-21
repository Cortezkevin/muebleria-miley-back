package com.furniture.miley.sales.dto.cart;

import com.furniture.miley.catalog.model.Product;
import com.furniture.miley.catalog.model.image.ProductImage;
import com.furniture.miley.sales.model.cart.CartItem;

import java.math.BigDecimal;
import java.util.List;

public record CartItemDTO(
        String id,
        String product_id,
        String name,
        String description,
        Integer stock,
        BigDecimal price,
        Integer amount,
        String category,
        BigDecimal total,
        String image
) {
    public static CartItemDTO fromEntity(CartItem cartItem){
        return new CartItemDTO(
                cartItem.getId(),
                cartItem.getProduct().getId(),
                cartItem.getProduct().getName(),
                cartItem.getProduct().getDescription(),
                cartItem.getProduct().getStock(),
                cartItem.getProduct().getPrice(),
                cartItem.getAmount(),
                cartItem.getProduct().getSubCategory().getCategory().getName(),
                cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(cartItem.getAmount())),
                getImagesFromDefaultOrColor(cartItem.getProduct()).get(0)
        );
    }

    private static List<String> getImagesFromDefaultOrColor(Product product){
        return product.getImages() != null && !product.getImages().isEmpty()
                ? product.getImages().stream().map(ProductImage::getUrl).toList()
                : product.getColors().getFirst()
                .getImages().stream().map(ProductImage::getUrl).toList();
    }
}
