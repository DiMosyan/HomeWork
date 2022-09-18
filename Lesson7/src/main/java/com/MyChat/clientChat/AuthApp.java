package com.MyChat.clientChat;

import com.MyChat.clientChat.controllers.AuthController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Timer;

public class AuthApp extends Application {

    private Stage authStage;
    private Stage regStage;
    private static AuthApp INSTANCE;

    public static void main(String[] args) {
        launch();
    }

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

        timerStart(8000);

        stage.setOnCloseRequest(windowEvent -> authController.disconnected());
    }

    public void initRegDialog() throws IOException {
        FXMLLoader regLoader = new FXMLLoader();
        regLoader.setLocation(AuthApp.class.getResource("RegApp-view.fxml"));
        AnchorPane regPane = regLoader.load();

        this.regStage = new Stage();
        this.regStage.initOwner(this.authStage);
        this.regStage.initModality(Modality.WINDOW_MODAL);
        this.regStage.setScene(new Scene(regPane));

        this.regStage.show();

        this.regStage.setOnCloseRequest(windowEvent -> timerStart(7000));
    }

    public void closeRegDialog() {
        this.regStage.close();
        timerStart(9000);
    }

    private void timerStart(int delay) {
        MyTimerTask timerTask = new MyTimerTask(delay);
        Timer timer = new Timer(true);
        timer.schedule(timerTask, 0);
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
}