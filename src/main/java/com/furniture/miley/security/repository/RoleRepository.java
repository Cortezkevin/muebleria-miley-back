package com.furniture.miley.security.repository;

import com.furniture.miley.security.model.Role;
import com.furniture.miley.security.enums.RolName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {

    Optional<Role> findByRolName(RolName rolName);
}
