package com.furniture.miley.config.cloudinary.dto;

import java.io.File;

public record UploadDTO(
        File file,
        String name
) {
}
