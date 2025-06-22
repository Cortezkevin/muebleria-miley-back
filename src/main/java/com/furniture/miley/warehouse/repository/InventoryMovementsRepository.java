package com.furniture.miley.warehouse.repository;

import com.furniture.miley.warehouse.model.InventoryMovements;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryMovementsRepository extends JpaRepository<InventoryMovements, String> {
}
