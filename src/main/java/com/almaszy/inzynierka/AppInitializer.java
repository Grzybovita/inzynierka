package com.almaszy.inzynierka;

import com.almaszy.inzynierka.entities.User;
import com.almaszy.inzynierka.services.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class AppInitializer
{
  private final UserService userService;

  public AppInitializer(UserService userService)
  {
    this.userService = userService;
  }

  @PostConstruct
  public void init()
  {
    System.out.println("Aplikacja została uruchomiona. Wykonywanie inicjalizacji...");
    /*User user = new User();
    user.setName("name1");
    user.setLogin("login");
    user.setPassword("password");
    this.userService.saveUser(user);*/

  }
}
