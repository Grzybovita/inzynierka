package com.almaszy.inzynierka.repositories;

import com.almaszy.inzynierka.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long>
{
  User findByLogin(String login);
  User findByEmail(String email);
  List<User> findByLoginAndPassword(String login, String password);
}
