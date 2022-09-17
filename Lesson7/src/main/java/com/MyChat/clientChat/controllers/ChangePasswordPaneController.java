package com.MyChat.clientChat.controllers;

import com.MyChat.clientChat.dialogs.Dialogs;
import com.MyChat.command.Command;
import com.MyChat.command.commands.ChangeUserDataCommandData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class ChangePasswordPaneController {

    @FXML
    public TextField passTextField;
    @FXML
    public TextField confPassTextField;
    @FXML
    public Button passwordChangeButton;

    private String currentNick;

    public void setCurrentNick(String currentNick) {
        this.currentNick = currentNick;
    }

    public void sendChangePassCommand(ActionEvent actionEvent) {
        if(passTextField.getText().isEmpty() || confPassTextField.getText().isEmpty()) {
            setEmptyText();
            Dialogs.ErrorType.REG_ERROR.show(Alert.AlertType.ERROR, "Enter the data!");
            return;
        }
        if(passTextField.getText().equals(confPassTextField.getText())) {
            ChatController.getInstance().sendExternalCommand(Command.changeUserDataCommand
                    (ChangeUserDataCommandData.TypeOfData.PASSWORD, passTextField.getText(), currentNick));
            setEmptyText();

        } else {
            setEmptyText();
            Dialogs.ErrorType.REG_ERROR.show(Alert.AlertType.ERROR, "Password's don't match!");
        }
    }

    private void setEmptyText() {
        passTextField.setText("");
        confPassTextField.setText("");
    }
}
