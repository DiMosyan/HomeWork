package com.MyChat.clientChat.controllers;

import com.MyChat.clientChat.ChatApp;
import com.MyChat.clientChat.dialogs.Dialogs;
import com.MyChat.command.Command;
import com.MyChat.command.CommandType;
import com.MyChat.command.commands.AuthOkCommandData;
import com.MyChat.command.commands.ErrorCommandData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class AuthController {

    //GUI field
    @FXML
    public Button authButton;

    @FXML
    public TextField loginField;

    @FXML
    public Label authMessage;

    @FXML
    public TextField passwordField;

    //code field
    private Stage stage;
    private final String SERVER_ADDR = "localhost";
    private final int PORT = 8189;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private boolean connected = false;

    private static AuthController INSTANCE;
    private boolean isAuthOk = false;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void connecting() {
        INSTANCE = this;
        try {
            socket = new Socket(SERVER_ADDR, PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            connected = true;
        } catch (IOException e) {
            Dialogs.ErrorType.NET_ERROR.show("Failed to establish a connection with the server");

            System.err.println("AuthController_ connecting");
        }
    }

    public void sendAuth(ActionEvent actionEvent) {
        if (isSendAuthMessage()) {
            authOk();
        }
    }

    private boolean isSendAuthMessage() {
        try {
            if (!loginField.getText().isEmpty() && !passwordField.getText().isEmpty()) {

                Command command = Command.authCommand(loginField.getText(), passwordField.getText());
                out.writeObject(command);

                if (!connected) {
                    Dialogs.ErrorType.NET_ERROR.show("Failed to establish a connection with the server");
                }
                return true;
            } else {
                Dialogs.ErrorType.AUTH_ERROR.show("Please, enter the data!");
                return false;
            }
        } catch (IOException e) {
            System.err.println("AuthController_isSendAuthMessage");
            e.printStackTrace();
            Dialogs.ErrorType.NET_ERROR.show("Failed to establish a connection with the server");
        }
        return false;
    }

    private void authOk() {
        try {
            Command answer = (Command) in.readObject();

            if (answer.getType() == CommandType.AUTH_OK) {
                AuthOkCommandData data = (AuthOkCommandData) answer.getData();
                ChatApp chatApp = new ChatApp(data.getUserName(), socket, in, out);
                chatApp.start(stage);
                isAuthOk = true;
            } else {
                loginField.setText("");
                passwordField.setText("");
                if (answer.getType() == CommandType.ERROR) {
                    ErrorCommandData data = (ErrorCommandData) answer.getData();
                    Dialogs.ErrorType.AUTH_ERROR.show(data.getErrorMessage());
                } else {
                    Dialogs.ErrorType.UNK_ERROR.show("Please contact the administrator");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Receive authorization message is wrong");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("Invalid command received");
        }
    }

    public void disconnected() {
        if(!connected) return;
        try {
            out.writeObject(Command.disconnectCommand());
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            System.err.println("Closing is wrong");
            e.printStackTrace();
        }
    }

    public static AuthController getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AuthController();
        }
        return INSTANCE;
    }

    public boolean getIsAuthOk() {
        return isAuthOk;
    }

    public Stage getStage() {
        return stage;
    }
}
