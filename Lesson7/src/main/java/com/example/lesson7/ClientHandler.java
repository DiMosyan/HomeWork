package com.example.lesson7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private MyServer server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String name;

    public String getName() {
        return name;
    }

    public ClientHandler(MyServer server, Socket socket) {
        this.server = server;
        this.socket = socket;
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    auth();
                    sendMessage(getNicksOfEntries());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                readMessage();
                            } catch (IOException e) {
                                System.err.println("Read message is wrong");
                                e.printStackTrace();
                            } finally {
                                closeConnection();
                            }
                        }
                    }).start();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void auth() {
        try {
            while (true) {
                String authStr = in.readUTF();
                if (authStr.startsWith("/auth")) {
                    String[] partsOfAuthStr = authStr.split(" ");
                    String nick = server.getAuthService().getNick(partsOfAuthStr[1], partsOfAuthStr[2]);
                    if(nick != null) {
                        if(server.isNickBusy(nick)) {
                            sendMessage("Account is busy");
                        } else {
                            sendMessage("/authIsOk " + nick);
                            name = nick;
                            server.addClient(this);
                            server.broadcastMessage("/con " + nick + " connected" + System.lineSeparator(), nick);
                            return;
                        }
                    } else {
                        sendMessage("Login/password is wrong");
                    }
                } else {
                    sendMessage("Command is wrong");
                }
            }
        } catch (IOException e) {
            System.err.println("error in the authorization block");
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            System.err.println("Send message is wrong");
            e.printStackTrace();
        }
    }

    private String getNicksOfEntries() {
        return server.getNicksOfEntries();
    }

    private void readMessage() throws IOException{
        String messageOfClient;

        while(true) {
            messageOfClient = in.readUTF();
            if(messageOfClient.equals("/end")) {
                sendMessage("/end");
                server.broadcastMessage(name + ": disconnected", name);
                return;
            }
            server.sendMessage(messageOfClient);
        }
    }

    private void closeConnection() {
        server.removeClient(this);
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            System.err.println("Disconnecting is wrong");
            e.printStackTrace();
        }
    }
}
