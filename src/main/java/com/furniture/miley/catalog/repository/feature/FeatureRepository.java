package com.furniture.miley.catalog.repository.feature;

import com.example.product_dinamic_stock.model.Feature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FeatureRepository extends JpaRepository<Feature, String> {
    Optional<Feature> findByName(String name);
}
