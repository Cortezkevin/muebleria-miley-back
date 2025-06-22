package com.furniture.miley.purchase.repository;

import com.furniture.miley.purchase.model.PurchaseOrderReception;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseOrderReceptionRepository extends JpaRepository<PurchaseOrderReception, String> {
}
