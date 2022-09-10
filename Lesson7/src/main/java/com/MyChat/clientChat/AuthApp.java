package com.MyChat.clientChat;

import com.MyChat.clientChat.controllers.AuthController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Timer;

public class AuthApp extends Application {

    private Stage authStage;
    private static AuthApp INSTANCE;

    @Override
    public void start(Stage stage) throws IOException {

        this.authStage = stage;

        FXMLLoader fxmlLoader = new FXMLLoader(AuthApp.class.getResource("AuthApp-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);

        AuthController authController = fxmlLoader.getController();
        authController.setStage(stage);
        authController.connecting();

        stage.show();

        MyTimerTask timerTask = new MyTimerTask();
        Timer timer = new Timer(true);
        timer.schedule(timerTask, 0);

        stage.setOnCloseRequest(windowEvent -> authController.disconnected());
    }

    @Override
    public void init() {
        INSTANCE = this;
    }

    public static AuthApp getInstance() {
        return INSTANCE;
    }

    public Stage getAuthStage() {
        return authStage;
    }

    public static void main(String[] args) {
        launch();
    }
}