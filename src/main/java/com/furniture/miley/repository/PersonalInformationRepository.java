package com.furniture.miley.repository;

import com.furniture.miley.model.PersonalInformation;
import com.furniture.miley.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonalInformationRepository extends JpaRepository<PersonalInformation, String> {
        Optional<PersonalInformation> findByUser(User user);
}
