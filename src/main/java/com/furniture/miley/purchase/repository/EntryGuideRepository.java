package com.furniture.miley.purchase.repository;

import com.furniture.miley.purchase.model.EntryGuide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntryGuideRepository extends JpaRepository<EntryGuide, String> {
}
