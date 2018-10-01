package ru.basanov.javacore.chatservice;

import ru.basanov.javacore.api.chatservice.AuthService;

import java.util.*;

public class BaseAuthService implements AuthService {
    private List<Entry> entries;

    private class Entry {
        private String login;
        private String pass;
        private String nick;

        public Entry(String login, String pass, String nick) {
            this.login = login;
            this.pass = pass;
            this.nick = nick;
        }

        public String getLogin() {
            return login;
        }
    }

    @Override
    public void start() {    }

    @Override
    public void stop() {    }

    public BaseAuthService() {
        entries = new ArrayList<Entry>();
        entries.add(new Entry("login1", "pass1", "nick1"));
        entries.add(new Entry("login2", "pass2", "nick2"));
        entries.add(new Entry("login3", "pass3", "nick3"));
    }

    @Override
    public String getNickByLoginPass(String login, String pass) {
        for (Entry o : entries) {
            if (o.getLogin().equals(login) && o.pass.equals(pass)) return o.nick;
        }
        return null;
    }
}
