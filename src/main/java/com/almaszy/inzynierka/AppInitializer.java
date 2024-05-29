package com.almaszy.inzynierka;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class AppInitializer
{

  public AppInitializer()
  {

  }

  @PostConstruct
  public void init()
  {
    System.out.println("Aplikacja została uruchomiona. Wykonywanie inicjalizacji...");
  }
}
