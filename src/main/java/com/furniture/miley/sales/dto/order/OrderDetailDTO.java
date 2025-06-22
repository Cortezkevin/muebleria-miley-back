package com.furniture.miley.sales.dto.order;

import com.furniture.miley.catalog.model.Product;
import com.furniture.miley.catalog.model.image.ProductImage;
import com.furniture.miley.sales.model.order.OrderDetail;

import java.math.BigDecimal;
import java.util.List;

public record OrderDetailDTO(
        String id,
        String image,
        String name,
        BigDecimal price,
        Integer amount,
        BigDecimal total
) {
    public static OrderDetailDTO toDTO(OrderDetail orderDetail){
        return new OrderDetailDTO(
                orderDetail.getId(),
                getImagesFromDefaultOrColor(orderDetail.getProduct()).get(0),
                orderDetail.getName(),
                orderDetail.getPrice(),
                orderDetail.getAmount(),
                orderDetail.getTotal()
        );
    }

    private static List<String> getImagesFromDefaultOrColor(Product product){
        return product.getImages() != null && !product.getImages().isEmpty()
                ? product.getImages().stream().map(ProductImage::getUrl).toList()
                : product.getColors().getFirst()
                .getImages().stream().map(ProductImage::getUrl).toList();
    }
}
