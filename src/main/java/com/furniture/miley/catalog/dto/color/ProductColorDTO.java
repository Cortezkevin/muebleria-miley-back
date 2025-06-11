package com.furniture.miley.catalog.dto.color;

import com.furniture.miley.catalog.dto.image.ProductImageDTO;
import com.furniture.miley.catalog.model.color.ProductColor;
import com.furniture.miley.catalog.model.image.ProductImage;

import java.util.List;

public record ProductColorDTO(
        String color,
        List<ProductImageDTO> images
) {
    public static ProductColorDTO toDTO(ProductColor productColor){
        return new ProductColorDTO(
                productColor.getColor().getColor(),
                productColor.getImages().stream().map(ProductImageDTO::toDTO).toList()
        );
    }
}
