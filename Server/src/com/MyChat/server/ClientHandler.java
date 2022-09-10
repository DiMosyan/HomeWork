package com.MyChat.server;

import com.MyChat.command.*;
import com.MyChat.command.commands.*;

import java.io.*;
import java.net.Socket;

public class ClientHandler {
    private MyServer server;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String name;

    public String getName() {
        return name;
    }

    public ClientHandler(MyServer server, Socket socket) {
        this.server = server;
        this.socket = socket;
        try {
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if(isAuth()) {
                        sendCommand(getNicksOfEntries());
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    readMessage();
                                } catch (IOException e) {
                                    System.err.println("Read message is wrong");
                                    e.printStackTrace();
                                } /*finally {
                                    closeConnection();
                                }*/
                            }
                        }).start();
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isAuth() {
        try {
            while (true) {
                Command command = readCommand();
                if(command == null) {
                    continue;
                }
                if(command.getType() == CommandType.AUTH) {
                    AuthCommandData authCommandData = (AuthCommandData) command.getData();
                    String nick = server.getAuthService().getNick(authCommandData.getLogin(), authCommandData.getPassword());
                    if(nick != null) {
                        if(server.isNickBusy(nick)) {
                            sendCommand(Command.errorCommand("Account is busy"));
                        } else {
                            sendCommand(Command.authOkCommand(nick));
                            this.name = nick;
                            server.addClient(this);
                            sendCommand(server.getNicksOfEntries());
                            server.updateUserList(this, Command.userListUpdateCommand(this.name, UserListUpdateCommandData.UserListUpdateType.ADD));
                            return true;
                        }
                    } else {
                        sendCommand(Command.errorCommand("Login/password is wrong"));
                    }
                } else {
                    if(command.getType() == CommandType.DISCONNECT) {
                        closeConnection();
                    } else {
                        sendCommand(Command.errorCommand("Command is wrong"));
                    }
                    return false;
                }
            }
        } catch (IOException e) {
            System.err.println("error in the authorization block");
            e.printStackTrace();
            return false;
        }
    }

    public void sendCommand(Command command) {
        try {
            out.writeObject(command);
        } catch (IOException e) {
            System.err.println("Send command is wrong");
            e.printStackTrace();
        }
    }

    private Command getNicksOfEntries() {
        return server.getNicksOfEntries();
    }

    private void readMessage() throws IOException{
        while(true) {
            Command command = readCommand();
            if(command == null) {
                continue;
            }
            switch (command.getType()) {
                case PRIVATE_MESSAGE: {
                    PrivateMessageCommandData data = (PrivateMessageCommandData) command.getData();
                    server.sendMessage(this, data.getReceiver(), data.getMessage());
                    break;
                }
                case PUBLIC_MESSAGE: {
                    PublicMessageCommandData data = (PublicMessageCommandData) command.getData();
                    server.broadcastMessage(this, data.getMessage());
                    break;
                }
                case DISCONNECT: {
                    server.updateUserList(this, Command.userListUpdateCommand(this.name, UserListUpdateCommandData.UserListUpdateType.DEL));
                    sendCommand(command);
                    closeConnection();
                    return;
                }
            }
        }
    }

    private Command readCommand() throws IOException {
        Command command = null;
        try {
            command = (Command) in.readObject();
        } catch (ClassNotFoundException e) {
            System.err.println("Failed to read Command");
            e.printStackTrace();
        }
        return command;
    }

    private void closeConnection() {
        server.removeClient(this);
        try {
            in.close();
            out.close();
            //socket.close();
        } catch (IOException e) {
            System.err.println("Disconnecting is wrong");
            e.printStackTrace();
        }
    }
}
