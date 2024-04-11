package com.almaszy.inzynierka.controllers;

import com.almaszy.inzynierka.entities.User;
import com.almaszy.inzynierka.services.UserService;
import com.google.gson.Gson;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
@RequestMapping("/register")
public class RegistrationController {

  private final UserService userService;
  private final Gson gson = new Gson();
  private static final java.util.logging.Logger logger = Logger.getLogger(RegistrationController.class.getName());

  public RegistrationController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping
  public ResponseEntity<String> registerUser(@RequestBody User newUser)
  {
    if (!userService.isLoginAvailable(newUser.getLogin()))
    {
      return ResponseEntity.badRequest().body(gson.toJson("loginTaken"));
    }
    else if (!userService.isEmailAvailable(newUser.getEmail()))
    {
      return ResponseEntity.badRequest().body(gson.toJson("emailTaken"));
    }
    else
    {
      userService.saveUser(newUser);
      logger.info("User: " + newUser + " saved to db");
    }
    return ResponseEntity.ok(gson.toJson("User registered successfully"));
  }
}
