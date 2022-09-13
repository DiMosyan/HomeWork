package com.MyChat.clientChat.controllers;

import com.MyChat.clientChat.dialogs.Dialogs;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class RegController {
    @FXML
    public TextField nickField;
    @FXML
    public TextField logField;
    @FXML
    public TextField passField;
    @FXML
    public TextField passConField;

    public void sendReg(ActionEvent actionEvent) {
        if(!isDataCorrect()) return;
        AuthController.getInstance().sendRegCommand(nickField.getText(), logField.getText(), passField.getText());
        setEmptyField();
    }

    private boolean isDataCorrect() {
        if(nickField.getText().isEmpty() || logField.getText().isEmpty() || passField.getText().isEmpty() || passConField.getText().isEmpty()) {
            Dialogs.ErrorType.REG_ERROR.show(Alert.AlertType.ERROR, "Please, enter the data!");
            setEmptyField();
            return false;
        }
        if(!passField.getText().equals(passConField.getText())) {
            Dialogs.ErrorType.REG_ERROR.show(Alert.AlertType.ERROR, "Passwords don't match!");
            setEmptyField();
            return false;
        }
        return true;
    }

    private void setEmptyField() {
        nickField.setText("");
        logField.setText("");
        passField.setText("");
        passConField.setText("");
    }


}
