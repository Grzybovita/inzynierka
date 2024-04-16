package com.almaszy.inzynierka.repositories;

import com.almaszy.inzynierka.entities.User;
import com.almaszy.inzynierka.models.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    VerificationToken findByToken(String token);

    VerificationToken findByUser(User user);
}
