package ru.basanov.javacore;

import ru.basanov.javacore.window.Chat;

public class ApplicationClient {
    public static void main(String[] args) {
        Chat chat = new Chat();
        chat.setSession(true);
        chat.setEndTimeMillis(System.currentTimeMillis() + 120000);
        chat.startChat("localhost",8189);
        chat.showChat();
        chat.sessionChat();
        while (chat.isSession()) {
            if (System.currentTimeMillis() > chat.getEndTimeMillis()) {
                System.out.println(System.currentTimeMillis());
                chat.closeChat();
            }
        }
    }
}
