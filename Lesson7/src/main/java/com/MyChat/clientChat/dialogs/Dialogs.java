package com.MyChat.clientChat.dialogs;

import com.MyChat.clientChat.AuthApp;
import javafx.scene.control.Alert;

public class Dialogs {
    public enum ErrorType {
        AUTH_ERROR("Authorization error!"),
        NET_ERROR("Network error!"),
        UNK_ERROR("Unknown error!"),
        AUTH_TIMEOUT("Time for authentication is over");

        private final String type;

        ErrorType(String type) {
            this.type = type;
        }

        public void show(String message) {
            showDialog(type, type, message);
        }
    }

    private static void showDialog(String title, String type, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(AuthApp.getInstance().getAuthStage());
        alert.setTitle(title);
        alert.setHeaderText(type);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
