package com.furniture.miley.catalog.repository.color;

import com.furniture.miley.catalog.model.color.Color;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ColorRepository extends JpaRepository<Color, String> {
    Optional<Color> findByColor(String color);
}
