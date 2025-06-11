package com.furniture.miley.catalog.dto.product;


import com.furniture.miley.catalog.dto.color.ColorImagesDTO;
import com.furniture.miley.catalog.dto.feature.ProductFeatureDTO;

import java.math.BigDecimal;
import java.util.List;

public record CreateProductDTO(
        String name,
        String description,
        BigDecimal price,
        Integer stock,
        String subcategoryId,
        List<ProductFeatureDTO> features,
        List<ColorImagesDTO> colorImages
) {
}
