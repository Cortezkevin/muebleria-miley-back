package com.furniture.miley.catalog.service;

import com.furniture.miley.catalog.model.color.Color;
import com.furniture.miley.catalog.repository.color.ColorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ColorService {
    private final ColorRepository mRepository;

    public Color findOrCreateByColor(String color){
        return mRepository.findByColor(color)
                .orElseGet(() -> mRepository.save(
                        Color.builder()
                                .color(color)
                                .build()
                ));
    }
}
