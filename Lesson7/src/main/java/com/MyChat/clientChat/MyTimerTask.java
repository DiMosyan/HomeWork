package com.MyChat.clientChat;

import com.MyChat.clientChat.controllers.AuthController;
import com.MyChat.clientChat.dialogs.Dialogs;
import javafx.application.Platform;

import java.util.TimerTask;

public class MyTimerTask extends TimerTask {

    @Override
    public void run() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            System.err.println("Timer's error");
            e.printStackTrace();
        }

        Platform.runLater(() -> {
            if(!AuthController.getInstance().getIsAuthOk()) {
                Dialogs.ErrorType.AUTH_TIMEOUT.show("Please, try later.");
                AuthController.getInstance().disconnected();
                AuthController.getInstance().getStage().close();
            }
        });
    }


}
