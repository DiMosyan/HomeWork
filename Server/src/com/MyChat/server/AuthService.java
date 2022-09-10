package com.MyChat.server;

import java.util.ArrayList;
import java.util.List;

public class AuthService {
    private class Entry {
        String login;
        String password;
        String nick;

        private Entry(String login, String password, String nick) {
            this.login = login;
            this.password = password;
            this.nick = nick;
        }
    }

    private final List<Entry> entries;

    public AuthService() {
        entries = new ArrayList<>();
        entries.add(new Entry("log1", "pass1", "nick1"));
        entries.add(new Entry("log2", "pass2", "nick2"));
        entries.add(new Entry("log3", "pass3", "nick3"));
    }
    
    public String getNick(String login, String password) {
        for (Entry e :
                entries) {
            if(e.login.equals(login) && e.password.equals(password)) return e.nick;
        }
        return null;
    }
}
