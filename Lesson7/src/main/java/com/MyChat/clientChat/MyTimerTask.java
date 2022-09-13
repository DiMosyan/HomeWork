package com.MyChat.clientChat;

import com.MyChat.clientChat.controllers.AuthController;
import com.MyChat.clientChat.dialogs.Dialogs;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.util.TimerTask;

public class MyTimerTask extends TimerTask {

    private final int delay;

    public MyTimerTask(int delay) {
        this.delay = delay;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            System.err.println("Timer's error");
            e.printStackTrace();
        }

        Platform.runLater(() -> {
            if(!AuthController.getInstance().getIsAuthOk()) {
                Dialogs.ErrorType.AUTH_TIMEOUT.show(Alert.AlertType.ERROR, "Please, try later.");
                AuthController.getInstance().disconnected();
                AuthController.getInstance().getStage().close();
            }
        });
    }


}
