package com.example.lesson7;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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
    private DataInputStream in;
    private DataOutputStream out;


    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void connecting() {
        try {
            socket = new Socket(SERVER_ADDR, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.err.println("Connecting is wrong");
            e.printStackTrace();
        }
    }

    public void sendAuth(ActionEvent actionEvent) {
        if(isSendAuthMessage()){
            authOk();
        }
    }

    private boolean isSendAuthMessage() {
        String messageToServer;
        try {
            if(!loginField.getText().isEmpty() && !passwordField.getText().isEmpty()) {
                messageToServer = "/auth " + loginField.getText() + " " + passwordField.getText();
                out.writeUTF(messageToServer);
                return true;
            } else {
                showWarningDialog("Please, enter the data");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Send message is wrong");
        }
        return false;
    }

    private void authOk() {
        try {
            String answer = in.readUTF();
            if(answer.startsWith("/authIsOk")) {
                String[] partsOfAnswer = answer.split(" ");


                ChatApp chatApp = new ChatApp(partsOfAnswer[1], in, out);
                chatApp.start(stage);
            } else {
                loginField.setText("");
                passwordField.setText("");
                showWarningDialog(answer);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Receive authorization message is wrong");
        }
    }

    public void showWarningDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning!");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
