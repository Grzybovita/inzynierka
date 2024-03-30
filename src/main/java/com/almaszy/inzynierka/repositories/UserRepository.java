package com.almaszy.inzynierka.repositories;

import com.almaszy.inzynierka.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long>
{

}
