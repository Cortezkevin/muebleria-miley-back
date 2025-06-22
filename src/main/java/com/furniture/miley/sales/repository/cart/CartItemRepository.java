package com.furniture.miley.sales.repository.cart;

import com.furniture.miley.catalog.model.Product;
import com.furniture.miley.sales.model.cart.Cart;
import com.furniture.miley.sales.model.cart.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, String> {
    List<CartItem> findByCart(Cart cart);
    List<CartItem> findByProduct(Product product);
    void deleteAllByCart(Cart cart);
}
