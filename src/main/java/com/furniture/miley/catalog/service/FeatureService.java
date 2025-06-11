package com.furniture.miley.catalog.service;

import com.furniture.miley.catalog.dto.feature.CreateFeatureDTO;
import com.furniture.miley.catalog.dto.feature.FeatureDTO;
import com.furniture.miley.catalog.model.feature.Feature;
import com.furniture.miley.catalog.repository.feature.FeatureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeatureService {
    private final FeatureRepository mRepository;

    public List<FeatureDTO> findAll(){
        return mRepository.findAll()
                .stream().map(FeatureDTO::toDTO)
                .toList();
    }

    public Feature findOrCreate(String featureName){
        return mRepository.findByName(featureName)
                .orElseGet(() -> mRepository.save(
                        Feature.builder()
                                .name(featureName)
                                .build()
                ));
    }

    public FeatureDTO create(CreateFeatureDTO createFeatureDTO){
        return FeatureDTO.toDTO( this.findOrCreate(createFeatureDTO.name()) );
    }
}