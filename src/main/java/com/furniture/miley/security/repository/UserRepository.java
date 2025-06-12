package com.furniture.miley.security.repository;

import com.furniture.miley.security.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email );
    Boolean existsByEmail(String email );
}
