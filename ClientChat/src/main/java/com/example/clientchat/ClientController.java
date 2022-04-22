package com.example.clientchat;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.text.DateFormat;
import java.util.Date;

public class ClientController {

    @FXML
    public TextField messageField;

    @FXML
    public Button sendButton;

    @FXML
    public TextArea messageTextArea;

    @FXML
    public ListView userList;

    public void sendMessage(ActionEvent actionEvent) {
        if (!messageField.getText().isEmpty()) {
            String sender = "I";

            if(!userList.getSelectionModel().isEmpty()) {
                sender = userList.getSelectionModel().getSelectedItem().toString();
            }
            messageTextArea.appendText(DateFormat.getDateInstance().format(new Date()) + System.lineSeparator());
            messageTextArea.appendText(sender + System.lineSeparator());
            messageTextArea.appendText(messageField.getText().trim() + System.lineSeparator());
            messageField.requestFocus();
            messageField.clear();
        }
    }
}