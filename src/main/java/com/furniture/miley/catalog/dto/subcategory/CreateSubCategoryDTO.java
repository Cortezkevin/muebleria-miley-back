package com.furniture.miley.catalog.dto.subcategory;

public record CreateSubCategoryDTO(
        String name,
        String description,
        String category_id
) {
}
