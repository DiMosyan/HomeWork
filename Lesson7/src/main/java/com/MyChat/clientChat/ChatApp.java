package com.MyChat.clientChat;

import com.MyChat.clientChat.controllers.ChatController;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.*;
import java.net.Socket;

public class ChatApp {
    private String name;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public ChatApp(String name, Socket socket, ObjectInputStream in, ObjectOutputStream out) {
        this.name = name;
        this.socket = socket;
        this.in = in;
        this.out = out;
    }

    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AuthApp.class.getResource("ChatApp-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);

        ChatController chatController = fxmlLoader.getController();
        chatController.start(name, socket, in, out);

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                chatController.sendDisconnectCommand();
            }
        });
    }
}
