package com.example.lesson7;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

public class MyServer {
    private final int PORT = 8189;
    private List<ClientHandler> clients = new ArrayList<>();
    private AuthService authService = new AuthService();

    public AuthService getAuthService() {
        return authService;
    }

    public MyServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                System.out.println("Server waiting for connection");
                Socket socket = serverSocket.accept();
                System.out.println("Client connected");
                new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Server is wrong");
        }
    }

    public boolean isNickBusy(String nick) {
        for (ClientHandler c :
                clients) {
            if(c.getName().equals(nick)) return true;
        }
        return false;
    }

    public synchronized void broadcastMessage(String message, String sender) {
        for (ClientHandler c :
                clients) {
            if(!c.getName().equals(sender)) c.sendMessage(message);
        }
    }

    public synchronized void addClient(ClientHandler client) {
        clients.add(client);
    }

    public synchronized String getNicksOfEntries() {
        String nicks = "/entries ";
        for (ClientHandler c :
                clients) {
            nicks += (c.getName() + " ");
        }

        return nicks;
    }

    public synchronized void sendMessage(String message) {
        String[] partsOfMessage = message.split(" ", 2);
        for (ClientHandler c :
                clients) {
            if(c.getName().equals(partsOfMessage[0])) {
                c.sendMessage(partsOfMessage[1]);
            }
        }
    }

    public synchronized void removeClient(ClientHandler client) {
        clients.remove(client);
    }
}
