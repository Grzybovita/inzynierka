package com.almaszy.inzynierka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class InzynierkaApplication
{
  //static { System.loadLibrary("jniortools");}

  public static void main(String[] args)
  {
    SpringApplication.run(InzynierkaApplication.class, args);
  }

}
