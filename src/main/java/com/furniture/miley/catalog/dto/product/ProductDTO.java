package com.furniture.miley.catalog.dto.product;

import com.furniture.miley.catalog.model.Product;
import com.furniture.miley.catalog.model.image.ProductImage;

import java.math.BigDecimal;
import java.util.List;

public record ProductDTO(
        String id,
        String name,
        String category,
        String subcategory,
        BigDecimal price,
        Integer stock,
        List<String> images
)
{
    public static ProductDTO toDTO(Product product){
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getSubCategory().getCategory().getName(),
                product.getSubCategory().getName(),
                product.getPrice(),
                product.getStock(),
                getImagesFromDefaultOrColor( product )
        );
    }

    private static List<String> getImagesFromDefaultOrColor(Product product){
        return product.getImages() != null && !product.getImages().isEmpty()
                ? product.getImages().stream().map(ProductImage::getUrl).toList()
                : product.getColors().getFirst()
                    .getImages().stream().map(ProductImage::getUrl).toList();
    }
}
