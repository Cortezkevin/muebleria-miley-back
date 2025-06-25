package com.furniture.miley.profile.repository;

import com.furniture.miley.profile.model.PersonalInformation;
import com.furniture.miley.security.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonalInformationRepository extends JpaRepository<PersonalInformation, String> {
    Optional<PersonalInformation> findByUser(User user);
}
