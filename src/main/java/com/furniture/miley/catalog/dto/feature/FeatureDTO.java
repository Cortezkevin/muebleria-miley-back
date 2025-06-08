package com.furniture.miley.catalog.dto.feature;

import com.furniture.miley.catalog.model.feature.Feature;

public record FeatureDTO(
        String id,
        String name
) {
    public static FeatureDTO toDTO(Feature feature){
        return new FeatureDTO(
                feature.getId(),
                feature.getName()
        );
    }
}
