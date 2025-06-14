package com.furniture.miley.sales.repository;

import com.furniture.miley.sales.model.Cart;
import com.furniture.miley.security.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, String> {
    Optional<Cart> findByUser(User user);
}
