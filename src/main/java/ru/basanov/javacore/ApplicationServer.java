package ru.basanov.javacore;

import ru.basanov.javacore.chatservice.BaseAuthService;
import ru.basanov.javacore.chatservice.MyServer;

public class ApplicationServer {

    public static void main(String[] args) {
        BaseAuthService baseAuthService = new BaseAuthService();
        MyServer myServer = new MyServer();
    }
}
