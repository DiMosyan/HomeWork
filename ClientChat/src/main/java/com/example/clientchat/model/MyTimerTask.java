package com.example.clientchat.model;

import com.example.clientchat.ClientChat;
import com.example.clientchat.dialogs.Dialogs;
import javafx.application.Platform;

import java.util.Date;
import java.util.TimerTask;

public class MyTimerTask extends TimerTask {

    @Override
    public void run() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (!Network.getInstance().isConnected()) {
                    Dialogs.AuthTimeout.AUTH_TIMEOUT.show();
                    ClientChat.getInstance().getAuthStage().close();
                    ClientChat.getInstance().getChatStage().close();
                }
            }
        });
    }

}
