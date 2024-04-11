package com.almaszy.inzynierka.controllers;

import com.almaszy.inzynierka.entities.User;
import com.almaszy.inzynierka.services.UserService;
import com.google.gson.Gson;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
@RequestMapping("/login")
public class LoginController {

  private final UserService userService;
  private final Gson gson = new Gson();
  private static final java.util.logging.Logger logger = Logger.getLogger(LoginController.class.getName());

  public LoginController(UserService userService)
  {
    this.userService = userService;
  }

  @PostMapping
  public ResponseEntity<String> loginUser(@RequestBody User loginUser)
  {
    if (!userService.verifyLoginCredentials(loginUser.getLogin(), loginUser.getPassword()))
    {
      return ResponseEntity.badRequest().body("invalidCredentials");
    }
    else
    {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      logger.info("User: " + loginUser + " logged in");
      return ResponseEntity.ok(gson.toJson("User logged in successfully"));
    }

  }
}
