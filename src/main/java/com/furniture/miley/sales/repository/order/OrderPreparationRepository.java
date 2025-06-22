package com.furniture.miley.sales.repository.order;

import com.furniture.miley.sales.model.order.Order;
import com.furniture.miley.sales.model.order.OrderPreparation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderPreparationRepository extends JpaRepository<OrderPreparation, String> {
    Optional<OrderPreparation> findByOrder(Order order);
}
