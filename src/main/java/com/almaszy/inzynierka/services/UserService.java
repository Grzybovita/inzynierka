package com.almaszy.inzynierka.services;

import com.almaszy.inzynierka.entities.User;
import com.almaszy.inzynierka.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService
{
  private final UserRepository userRepository;
  private final EncryptionService encryptionService;

  @Autowired
  public UserService(UserRepository userRepository, EncryptionService encryptionService)
  {
    this.userRepository = userRepository;
    this.encryptionService = encryptionService;
  }

  public User saveUser(User user)
  {
    String encryptedPassword = encryptionService.encryptPassword(user.getPassword());
    user.setPassword(encryptedPassword);
    return userRepository.save(user);
  }

  public boolean isLoginAvailable(String login)
  {
    return userRepository.findByLogin(login) == null;
  }

  public boolean isEmailAvailable(String email)
  {
    return userRepository.findByEmail(email) == null;
  }

  public boolean verifyLoginCredentials(String login, String password)
  {
    User userFromDb = userRepository.findByLogin(login);
    if (userFromDb != null)
    {
      return encryptionService.checkPassword(password, userFromDb.getPassword());
    }

    return false;
  }
}
