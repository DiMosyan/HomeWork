package com.example.lesson7;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatApp {
    private String name;
    private DataInputStream in;
    private DataOutputStream out;

    public ChatApp(String name, DataInputStream in, DataOutputStream out) {
        this.name = name;
        this.in = in;
        this.out = out;
    }

    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AuthApp.class.getResource("ChatApp-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);

        ChatController chatController = fxmlLoader.getController();
        chatController.start(name, in, out);

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                chatController.disconnected();
            }
        });
    }
}
