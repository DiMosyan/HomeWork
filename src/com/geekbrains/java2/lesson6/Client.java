package com.geekbrains.java2.lesson6;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private final String SERVER_ADDR = "localhost";
    private final int PORT = 8189;

    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }

    public void start() {
        try {
            Socket socket = new Socket(SERVER_ADDR, PORT);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            receiveMessage(in);
            sendMessage(out);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Connection is lost");
        }
    }

    private void sendMessage(DataOutputStream out) {
        String messageToServer;
        Scanner sc = new Scanner(System.in);

        try {
            while (true) {
                messageToServer = sc.nextLine();
                if (!messageToServer.isEmpty()) {
                    out.writeUTF(messageToServer);
                }
                if (messageToServer.startsWith("/end")) {
                    System.out.println("End of work");
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Send is wrong");
        }
    }

    private void receiveMessage(DataInputStream in) {
        Thread receiveThread = new Thread(new Runnable() {
            String messageFromServer;

            @Override
            public void run() {

                try {
                    while (true) {
                        messageFromServer = in.readUTF();
                        System.out.println("Message from Server: " + messageFromServer);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("Receive is wrong");
                }
            }
        });

        receiveThread.setDaemon(true);
        receiveThread.start();
    }
}
