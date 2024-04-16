package com.almaszy.inzynierka.repositories;

import com.almaszy.inzynierka.models.ERole;
import com.almaszy.inzynierka.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(ERole name);
}
