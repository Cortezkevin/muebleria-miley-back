package com.furniture.miley.catalog.dto.category;

public record UpdateCategoryDTO(
        String id,
        String newName,
        String newDescription
) {
}
