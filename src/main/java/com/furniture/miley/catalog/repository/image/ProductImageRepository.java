package com.furniture.miley.catalog.repository.image;

import com.furniture.miley.catalog.model.color.ProductColor;
import com.furniture.miley.catalog.model.image.ColorProductImage;
import com.furniture.miley.catalog.model.image.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, String> {
    List<ColorProductImage> findByProductColor(ProductColor productColor);
}
