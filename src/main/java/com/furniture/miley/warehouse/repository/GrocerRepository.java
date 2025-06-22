package com.furniture.miley.warehouse.repository;

import com.furniture.miley.security.model.User;
import com.furniture.miley.warehouse.model.Grocer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GrocerRepository extends JpaRepository<Grocer, String> {
    Optional<Grocer> findByUser(User user);
}
