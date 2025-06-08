package com.furniture.miley.catalog.dto.feature;

import com.furniture.miley.catalog.model.feature.ProductFeature;

public record ProductFeatureDTO(
        String feature,
        String value
) {
    public static ProductFeatureDTO toDTO(ProductFeature productFeature){
        return new ProductFeatureDTO(
                productFeature.getFeature().getName(),
                productFeature.getValue()
        );
    }
}
