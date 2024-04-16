package com.almaszy.inzynierka.services;

import com.almaszy.inzynierka.AppConstants;
import com.almaszy.inzynierka.entities.User;
import com.almaszy.inzynierka.models.VerificationToken;
import com.almaszy.inzynierka.repositories.UserRepository;
import com.almaszy.inzynierka.repositories.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.UUID;

@Service
public class UserService
{
  private final UserRepository userRepository;
  private final EncryptionService encryptionService;
  private final VerificationTokenRepository tokenRepository;
  MailService mailService;

  @Autowired
  public UserService(UserRepository userRepository, EncryptionService encryptionService, VerificationTokenRepository tokenRepository)
  {
    this.userRepository = userRepository;
    this.encryptionService = encryptionService;
    this.tokenRepository = tokenRepository;
  }

  public User saveUser(User user)
  {
    String encryptedPassword = encryptionService.encryptPassword(user.getPassword());
    user.setPassword(encryptedPassword);
    return userRepository.save(user);
  }

  public boolean isUsernameAvailable(String username)
  {
    return userRepository.findByUsername(username).isPresent();
  }

  public boolean isEmailAvailable(String email)
  {
    return userRepository.findByEmail(email) == null;
  }

  public boolean verifyLoginCredentials(String username, String password)
  {
    User userFromDb = userRepository.findByUsername(username).get();
    if (userFromDb != null)
    {
      return encryptionService.checkPassword(password, userFromDb.getPassword());
    }

    return false;
  }

  public void createVerificationTokenForUser(final User user, final String token)
  {
    final VerificationToken myToken = new VerificationToken(token, user);
    tokenRepository.save(myToken);
  }

  @Transactional
  public boolean resendVerificationToken(final String existingVerificationToken)
  {
    VerificationToken vToken = tokenRepository.findByToken(existingVerificationToken);
    if(vToken != null)
    {
      vToken.updateToken(UUID.randomUUID().toString());
      vToken = tokenRepository.save(vToken);
      mailService.sendVerificationToken(vToken.getToken(), vToken.getUser());
      return true;
    }
    return false;
  }

  public String validateVerificationToken(String token)
  {
    final VerificationToken verificationToken = tokenRepository.findByToken(token);
    if (verificationToken == null)
    {
      return AppConstants.TOKEN_INVALID;
    }

    final User user = verificationToken.getUser();
    final Calendar cal = Calendar.getInstance();
    if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0)
    {
      return AppConstants.TOKEN_EXPIRED;
    }

    user.setEnabled(true);
    tokenRepository.delete(verificationToken);
    userRepository.save(user);
    return AppConstants.TOKEN_VALID;
  }
}
