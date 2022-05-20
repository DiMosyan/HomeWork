package com.example.server;

import com.example.server.chat.MyServer;

public class StartServer {

    public static void main(String[] args) {
        new MyServer().start(8189);
    }
}
