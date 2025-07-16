package com.furniture.miley.sales.repository.order;

import com.furniture.miley.delivery.model.OrderShippingLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderShippingLocationRepository extends JpaRepository<OrderShippingLocation, String> {
}
