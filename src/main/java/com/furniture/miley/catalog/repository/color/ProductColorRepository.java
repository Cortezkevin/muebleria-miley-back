package com.furniture.miley.catalog.repository.color;

import com.furniture.miley.catalog.model.color.Color;
import com.furniture.miley.catalog.model.Product;
import com.furniture.miley.catalog.model.color.ProductColor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductColorRepository extends JpaRepository<ProductColor, String> {
    Optional<ProductColor> findByProductAndColor(Product product, Color color);
}
