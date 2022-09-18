package com.MyChat.clientChat;

import com.MyChat.clientChat.controllers.ChangeDataPaneController;
import com.MyChat.clientChat.controllers.ChangePasswordPaneController;
import com.MyChat.clientChat.controllers.ChatController;
import com.MyChat.command.Command;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class ChatApp {
    private String name;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private static ChatApp INSTANCE;
    private Stage chatStage;
    private Stage changeDataStage;

    public ChatApp(String name, Socket socket, ObjectInputStream in, ObjectOutputStream out) {
        this.name = name;
        this.socket = socket;
        this.in = in;
        this.out = out;
    }

    public void start(Stage stage) throws IOException {
        this.chatStage = stage;
        INSTANCE = this;
        FXMLLoader fxmlLoader = new FXMLLoader(AuthApp.class.getResource("ChatApp-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);

        ChatController chatController = fxmlLoader.getController();
        chatController.start(name, socket, in, out);

        stage.setOnCloseRequest(windowEvent -> chatController.sendExternalCommand(Command.disconnectCommand()));
    }

    public static ChatApp getInstance() {
        return INSTANCE;
    }

    public void initDialog(String resource, String typeOfData, String name) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(AuthApp.class.getResource(resource));
        AnchorPane pane = loader.load();

        changeDataStage = new Stage();
        changeDataStage.initOwner(chatStage);
        changeDataStage.initModality(Modality.WINDOW_MODAL);
        changeDataStage.setScene(new Scene(pane));

        if(typeOfData.equals("password")) {
            ChangePasswordPaneController controller = loader.getController();
            controller.setCurrentNick(name);
        } else {
            ChangeDataPaneController controller = loader.getController();
            controller.start(name, typeOfData);
        }

        changeDataStage.show();
    }

    public Stage getChangeDataStage() {
        return changeDataStage;
    }
}
