package com.furniture.miley.catalog.dto.subcategory;

public record UpdateSubCategoryDTO(
        String id,
        String newName,
        String newDescription,
        String newCategoryId
) {
}
