package com.furniture.miley.purchase.repository;

import com.furniture.miley.purchase.model.RejectionGuide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RejectionGuideRepository extends JpaRepository<RejectionGuide, String> {
}
