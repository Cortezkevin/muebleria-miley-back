package com.furniture.miley.catalog.dto.category;

import com.furniture.miley.catalog.dto.subcategory.SubCategoryDTO;
import com.furniture.miley.catalog.model.Category;

import java.util.List;
import java.util.stream.Collectors;

public record DetailedCategoryDTO(
        String id,
        String name,
        String description,
        String url_image,
        List<SubCategoryDTO> subCategoryList
) {
    public static DetailedCategoryDTO toDTO(Category category){
        return new DetailedCategoryDTO(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getUrl_image(),
                category.getSubCategoryList().stream().map( SubCategoryDTO::toDTO ).collect(Collectors.toList())
        );
    }
}
