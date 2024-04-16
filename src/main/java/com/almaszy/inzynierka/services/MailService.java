package com.almaszy.inzynierka.services;


import com.almaszy.inzynierka.entities.User;

public interface MailService {

    void sendVerificationToken(String token, User user);
}
