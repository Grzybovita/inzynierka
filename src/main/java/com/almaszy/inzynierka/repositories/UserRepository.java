package com.almaszy.inzynierka.repositories;

import com.almaszy.inzynierka.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>
{
  //User findByLogin(String login);
  User findByEmail(String email);
  Optional<User> findByUsername(String username);
  List<User> findByUsernameAndPassword(String login, String password);
  boolean existsByUsername(String username);

  boolean existsByEmail(String email);
}
