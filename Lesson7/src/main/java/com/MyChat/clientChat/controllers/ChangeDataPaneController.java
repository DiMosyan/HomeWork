package com.MyChat.clientChat.controllers;

import com.MyChat.clientChat.dialogs.Dialogs;
import com.MyChat.command.Command;
import com.MyChat.command.commands.ChangeUserDataCommandData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ChangeDataPaneController {
    @FXML
    public Label invitationLabel;
    @FXML
    public Label headingLabel;
    @FXML
    public TextField newDataTextField;
    @FXML
    public Button changeButton;

    private String type;
    private String currentNick;

    public void start(String nick, String type) {
        this.currentNick = nick;
        this.type = type;
        headingLabel.setText("Changing " + type);
        invitationLabel.setText("Enter new " + type);
    }

    public void changeNickSendCommand(ActionEvent actionEvent) {
        if(newDataTextField.getText().isEmpty()) Dialogs.ErrorType.REG_ERROR.show(Alert.AlertType.ERROR, "Enter the data!");
        else {
            if(this.type.equals("nick")) {
                if(newDataTextField.getText().equals(this.currentNick)) {
                    newDataTextField.setText("");
                    Dialogs.ErrorType.REG_ERROR.show(Alert.AlertType.ERROR, "The new nick is the same as the old one.");
                    return;
                }
                ChatController.getInstance().sendExternalCommand(Command.changeUserDataCommand
                        (ChangeUserDataCommandData.TypeOfData.NICK, newDataTextField.getText(), currentNick));
            }
            else ChatController.getInstance().sendExternalCommand(Command.changeUserDataCommand
                    (ChangeUserDataCommandData.TypeOfData.LOGIN, newDataTextField.getText(), currentNick));
            newDataTextField.setText("");
        }
    }
}
