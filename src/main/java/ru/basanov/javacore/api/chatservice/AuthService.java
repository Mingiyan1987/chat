package ru.basanov.javacore.api.chatservice;

public interface AuthService {
    void start();
    String getNickByLoginPass(String login, String pass);
    void stop();
}
