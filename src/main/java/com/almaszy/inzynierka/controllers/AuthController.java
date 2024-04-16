package com.almaszy.inzynierka.controllers;

import com.almaszy.inzynierka.ApiResponse;
import com.almaszy.inzynierka.AppConstants;
import com.almaszy.inzynierka.entities.User;
import com.almaszy.inzynierka.models.ERole;
import com.almaszy.inzynierka.models.Role;
import com.almaszy.inzynierka.payload.request.LoginRequest;
import com.almaszy.inzynierka.payload.request.SignupRequest;
import com.almaszy.inzynierka.payload.response.MessageResponse;
import com.almaszy.inzynierka.payload.response.UserInfoResponse;
import com.almaszy.inzynierka.repositories.RoleRepository;
import com.almaszy.inzynierka.repositories.UserRepository;
import com.almaszy.inzynierka.security.jwt.JwtUtils;
import com.almaszy.inzynierka.security.services.UserDetailsImpl;
import com.almaszy.inzynierka.services.MailService;
import com.almaszy.inzynierka.services.UserService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials="true")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  @Autowired
  UserService userService;

  @Autowired
  MailService mailService;

  private final Gson gson = new Gson();

  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

    try
    {
      Authentication authentication = authenticationManager
              .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

      SecurityContextHolder.getContext().setAuthentication(authentication);

      UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

      ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

      List<String> roles = userDetails.getAuthorities().stream()
              .map(item -> item.getAuthority())
              .collect(Collectors.toList());

      return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
              .body(new UserInfoResponse(userDetails.getId(),
                      userDetails.getUsername(),
                      userDetails.getEmail(),
                      roles));
    }
    catch (Exception e)
    {
      return ResponseEntity.badRequest().body(gson.toJson("invalidCredentials"));
    }
  }

  @PostMapping("/signup")
  public ResponseEntity<String> registerUser(@RequestBody SignupRequest signUpRequest)
  {
    if (userRepository.existsByUsername(signUpRequest.getUsername()))
    {
      return ResponseEntity.badRequest().body(gson.toJson("usernameTaken"));
    }

    if (userRepository.existsByEmail(signUpRequest.getEmail()))
    {
      return ResponseEntity.badRequest().body(gson.toJson("emailTaken"));
    }

    // Create new user's account
    User user = new User(signUpRequest.getUsername(),
            signUpRequest.getEmail(),
            encoder.encode(signUpRequest.getPassword()));

    Set<String> strRoles = signUpRequest.getRole();
    Set<Role> roles = new HashSet<>();

    if (strRoles == null)
    {
      Role userRole = roleRepository.findByName(ERole.ROLE_USER)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
      roles.add(userRole);
    }
    else
    {
      strRoles.forEach(role -> {
        switch (role) {
          case "admin":
            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(adminRole);

            break;
          case "mod":
            Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(modRole);

            break;
          default:
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        }
      });
    }

    user.setRoles(roles);
    userRepository.save(user);
    //should we do it here?
    final String token = UUID.randomUUID().toString();
    userService.createVerificationTokenForUser(user, token);
    mailService.sendVerificationToken(token, user);

    return ResponseEntity.ok(gson.toJson("ok"));
  }

  @PostMapping("/signout")
  public ResponseEntity<?> logoutUser()
  {
    ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
    return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(new MessageResponse("You've been signed out!"));
  }

  @PostMapping("/token/verify")
  public ResponseEntity<?> confirmRegistration(@RequestBody String token)
  {
    final String result = userService.validateVerificationToken(token);
    return ResponseEntity.ok().body(new ApiResponse(true, result));
  }

  // user activation - verification
  @PostMapping("/token/resend")
  @ResponseBody
  public ResponseEntity<?> resendRegistrationToken(@RequestBody String expiredToken)
  {
    if (!userService.resendVerificationToken(expiredToken))
    {
      return new ResponseEntity<>(new ApiResponse(false, "Token not found!"), HttpStatus.BAD_REQUEST);
    }
    return ResponseEntity.ok().body(new ApiResponse(true, AppConstants.SUCCESS));
  }
}
