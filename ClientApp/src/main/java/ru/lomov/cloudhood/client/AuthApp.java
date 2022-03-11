package ru.lomov.cloudhood.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class AuthApp extends Application {


    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/ru/lomov/cloudhood/client/main_view.fxml"));
        Scene authScene = new Scene(root);
        authScene.setFill(Color.TRANSPARENT);
        stage.setScene(authScene);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.getIcons().add(
                new Image(AuthApp.class.getResourceAsStream("/ru/lomov/cloudhood/client/img/Icon.png")));

        stage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
