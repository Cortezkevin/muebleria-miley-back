package com.furniture.miley.catalog.dto.color;

import java.util.List;

public record ColorImagesDTO(
        String color,
        List<String> fileNames
) {
}
