package com.geekbrains.java2.lesson6;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    private final int PORT = 8189;

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }


    public void start() {
        Socket socket = null;

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is ready");
            socket = serverSocket.accept();
            System.out.println("Client is ready");
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            sendMessage(out);
            receiveMessage(in);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Connection is lost");
        }
    }

    private void sendMessage(DataOutputStream out) {
        Scanner sc = new Scanner(System.in);

        Thread sendThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String messageToClient;

                try {
                    while (true) {
                        messageToClient = sc.nextLine();
                        if(!messageToClient.isEmpty()) {
                            out.writeUTF(messageToClient);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("Send is wrong");
                }
            }
        });
        sendThread.setDaemon(true);
        sendThread.start();
    }

    private void receiveMessage(DataInputStream in) {
        String messageFromClient;

        try {
            while (true) {
                messageFromClient = in.readUTF();
                if(messageFromClient.startsWith("/end")) {
                    System.out.println("End of work");
                    return;
                }
                System.out.println("Message of client: " + messageFromClient);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Receive is wrong");
        }
    }
}
