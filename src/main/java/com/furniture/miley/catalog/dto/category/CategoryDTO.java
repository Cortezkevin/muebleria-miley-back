package com.furniture.miley.catalog.dto.category;
import com.furniture.miley.catalog.model.Category;


public record CategoryDTO(
        String id,
        String name,
        String description,
        String url_image
) {
    public static CategoryDTO toDTO(Category category){
        return new CategoryDTO(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getUrl_image()
        );
    }
}
