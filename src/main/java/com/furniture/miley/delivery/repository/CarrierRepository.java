package com.furniture.miley.delivery.repository;

import com.furniture.miley.delivery.model.Carrier;
import com.furniture.miley.security.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarrierRepository extends JpaRepository<Carrier, String> {
    Optional<Carrier> findByUser(User user);
}
