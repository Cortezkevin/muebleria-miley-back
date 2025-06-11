package com.furniture.miley.catalog.dto.image;

import com.furniture.miley.catalog.model.image.ProductImage;

public record ProductImageDTO(
        String url,
        String imageId
) {
    public static ProductImageDTO toDTO(ProductImage productImage){
        return new ProductImageDTO(
                productImage.getUrl(),
                productImage.getImage_id()
        );
    }
}
