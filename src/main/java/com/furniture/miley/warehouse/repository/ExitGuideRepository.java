package com.furniture.miley.warehouse.repository;

import com.furniture.miley.warehouse.model.ExitGuide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExitGuideRepository extends JpaRepository<ExitGuide, String> {
}
