package com.furniture.miley.catalog.dto.product;

import com.furniture.miley.catalog.dto.subcategory.SubCategoryDTO;
import com.furniture.miley.catalog.enums.AcquisitionType;
import com.furniture.miley.catalog.model.Product;
import com.furniture.miley.catalog.model.image.ProductImage;

import java.math.BigDecimal;
import java.util.List;

public record ProductDTO(
        String id,
        String name,
        String description,
        String category,
        SubCategoryDTO subcategory,
        BigDecimal price,
        Integer stock,
        AcquisitionType acquisitionType,
        List<String> images
)
{
    public static ProductDTO toDTO(Product product){
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getSubCategory().getCategory().getName(),
                SubCategoryDTO.toDTO(product.getSubCategory()),
                product.getPrice(),
                product.getStock(),
                product.getAcquisitionType(),
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
