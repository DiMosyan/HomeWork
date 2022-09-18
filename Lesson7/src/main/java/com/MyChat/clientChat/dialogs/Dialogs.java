package com.MyChat.clientChat.dialogs;

import com.MyChat.clientChat.AuthApp;
import javafx.scene.control.Alert;

public class Dialogs {
    public enum ErrorType {
        AUTH_ERROR("Authorization error!"),
        NET_ERROR("Network error!"),
        UNK_ERROR("Unknown error!"),
        AUTH_TIMEOUT("Time for authentication is over"),
        SEND_COMMAND_ERROR("Command sending error."),
        REG_ERROR("Registration error!"),
        SUCCESS_REG("Registration is success!");

        private final String type;

        ErrorType(String type) {
            this.type = type;
        }

        public void show(Alert.AlertType alertType, String message) {
            showDialog(alertType, type, type, message);
        }
    }

    private static void showDialog(Alert.AlertType alertType, String title, String type, String message) {
        Alert alert = new Alert(alertType);
        alert.initOwner(AuthApp.getInstance().getAuthStage());
        alert.setTitle(title);
        alert.setHeaderText(type);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
