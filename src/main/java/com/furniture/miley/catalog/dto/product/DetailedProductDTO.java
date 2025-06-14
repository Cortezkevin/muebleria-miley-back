package com.furniture.miley.catalog.dto.product;

import com.furniture.miley.catalog.dto.color.ProductColorDTO;
import com.furniture.miley.catalog.dto.feature.ProductFeatureDTO;
import com.furniture.miley.catalog.enums.AcquisitionType;
import com.furniture.miley.catalog.model.Product;
import com.furniture.miley.catalog.model.image.ProductImage;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public record DetailedProductDTO(
        String id,
        String name,
        String description,
        String category,
        String subcategory,
        BigDecimal price,
        Integer stock,
        List<String> images,
        List<ProductColorDTO> colors,
        List<ProductFeatureDTO> features,
        AcquisitionType acquisitionType //TODO: Luego se agregara mas entidades relacionadas, como: Marca, Proveedor, en caso el tipo de adquisicion sea COMPRADO (BOUGHT) osea que no es fabricado por la empresa
) {
    public static DetailedProductDTO toDTO(Product product){
        return new DetailedProductDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getSubCategory().getCategory().getName(),
                product.getSubCategory().getName(),
                product.getPrice(),
                product.getStock(),
                product.getImages() == null || product.getImages().isEmpty() ? new ArrayList<>() : product.getImages().stream().map(ProductImage::getUrl).toList(),
                product.getColors() == null || product.getColors().isEmpty() ? new ArrayList<>() : product.getColors().stream().map(ProductColorDTO::toDTO).toList(),
                product.getFeatures().stream().map(ProductFeatureDTO::toDTO).toList(),
                product.getAcquisitionType()
        );
    }
}
