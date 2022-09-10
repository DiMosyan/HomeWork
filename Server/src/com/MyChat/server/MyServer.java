package com.MyChat.server;

import com.MyChat.command.Command;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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

    public synchronized void broadcastMessage(ClientHandler sender, String message) {
        for(ClientHandler client : clients) {
            if(client != sender) {
                client.sendCommand(Command.publicMessageCommand(message, sender.getName()));
            }
        }
    }

    public synchronized void updateUserList(ClientHandler sender, Command command) {
        for(ClientHandler client : clients) {
            if(client != sender) {
                client.sendCommand(command);
            }
        }
    }

    public synchronized void addClient(ClientHandler client) {
        clients.add(client);
    }

    public synchronized Command getNicksOfEntries() {
        List<String> clientsNames = new ArrayList<>();
        clientsNames.add("All");
        for (ClientHandler c : clients) {
            clientsNames.add(c.getName());
        }

        return Command.userListInitCommand(clientsNames);
    }

    public synchronized void sendMessage(ClientHandler sender, String recipient, String message) {
        if(sender.getName().equals(recipient)) {
            return;
        }
        for(ClientHandler client : clients) {
            if(client.getName().equals(recipient)) {
                client.sendCommand(Command.privateMessageCommand(message, recipient, sender.getName()));
            }
        }
    }

    public synchronized void removeClient(ClientHandler client) {
        clients.remove(client);
    }
}
