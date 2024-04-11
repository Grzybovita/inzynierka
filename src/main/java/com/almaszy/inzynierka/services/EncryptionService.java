package com.almaszy.inzynierka.services;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class EncryptionService {

  private final BCryptPasswordEncoder passwordEncoder;

  public EncryptionService()
  {
    this.passwordEncoder = new BCryptPasswordEncoder();
  }

  public String encryptPassword(String password)
  {
    return passwordEncoder.encode(password);
  }

  public boolean checkPassword(String plainPassword, String encryptedPassword)
  {
    return passwordEncoder.matches(plainPassword, encryptedPassword);
  }
}
