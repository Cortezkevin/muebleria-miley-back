package com.furniture.miley.catalog.dto.subcategory;

import com.furniture.miley.catalog.dto.category.CategoryDTO;
import com.furniture.miley.catalog.model.SubCategory;

public record SubCategoryDTO (
        String id,
        String name,
        String description,
        CategoryDTO category,
        String url_image
){
    public static SubCategoryDTO toDTO(SubCategory subCategory){
        return new SubCategoryDTO(
                subCategory.getId(),
                subCategory.getName(),
                subCategory.getDescription(),
                CategoryDTO.toDTO( subCategory.getCategory() ),
                subCategory.getUrl_image()
        );
    }
}
