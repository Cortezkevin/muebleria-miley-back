package com.furniture.miley.profile.repository;

import com.furniture.miley.profile.model.Address;
import com.furniture.miley.profile.model.PersonalInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, String> {
    Optional<Address> findByPersonalInformation(PersonalInformation personalInformation);
}
