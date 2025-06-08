package com.furniture.miley.catalog.dto.product;

import com.furniture.miley.catalog.dto.color.ProductColorDTO;
import com.furniture.miley.catalog.dto.feature.ProductFeatureDTO;
import com.furniture.miley.catalog.model.Product;
import com.furniture.miley.catalog.model.image.ProductImage;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public record ProductDTO(
        String id,
        String name,
        BigDecimal price,
        List<String> images,
        List<ProductColorDTO> colors,//
        List<ProductFeatureDTO> features//
)
{
    public static ProductDTO toDTO(Product product){
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getImages() == null || product.getImages().isEmpty() ? new ArrayList<>() : product.getImages().stream().map(ProductImage::getUrl).toList(),
                product.getColors() == null || product.getColors().isEmpty() ? new ArrayList<>() : product.getColors().stream().map(ProductColorDTO::toDTO).toList(),
                product.getFeatures().stream().map(ProductFeatureDTO::toDTO).toList()
        );
    }
}
