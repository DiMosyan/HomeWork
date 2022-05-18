package com.example.lesson7;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AuthApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AuthApp.class.getResource("AuthApp-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);

        AuthController authController = fxmlLoader.getController();
        authController.setStage(stage);
        authController.connecting();

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}