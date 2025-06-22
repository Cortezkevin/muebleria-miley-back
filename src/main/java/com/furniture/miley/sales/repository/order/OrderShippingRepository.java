package com.furniture.miley.sales.repository.order;

import com.furniture.miley.sales.model.order.Order;
import com.furniture.miley.sales.model.order.OrderShipping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderShippingRepository extends JpaRepository<OrderShipping, String> {
    Optional<OrderShipping> findByOrder(Order order);
}
