package ru.lomov.cloudhood.client.controllers;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import ru.lomov.cloudhood.client.AuthApp;
import ru.lomov.cloudhood.client.ClientNetworkNetty;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class AuthFormViewController implements Initializable {
    @FXML
    private Button closeBTN;
    @FXML
    private AnchorPane changePasswordPane;
    @FXML
    private PasswordField passwordLoginField1;
    @FXML
    private Button changePassword;
    @FXML
    private Label alertLabel;
    @FXML
    private TextField uidField;
    @FXML
    private AnchorPane mainPane;
    @FXML
    private TextField loginRegField;
    @FXML
    private PasswordField passwordRegField;
    @FXML
    private PasswordField passwordRegFieldRe;
    @FXML
    private TextField nicknameRegField;
    @FXML
    private Button signUpBTN;
    @FXML
    private PasswordField passwordLoginField;
    @FXML
    private TextField loginLoginField;
    @FXML
    private Button signInBTN;
    @FXML
    private AnchorPane contentPane;
    @FXML
    private Button signUpFormBTN;
    @FXML
    private Button signInFormBTN;
    private Parent fxml;
    private static ClientNetworkNetty network;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Итерация play();");
        network = new ClientNetworkNetty();
    }


    /*
     * Анимация форм---------------------------------------------------------------------------------------------------------------------------
     * */
    public void openSignInForm() {
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(1), contentPane);
        translateTransition.setToX(contentPane.getLayoutX() * 17.5);
        translateTransition.play();
        translateTransition.setOnFinished((e) -> {
            try {
                contentPane.getChildren().removeAll();
                fxml = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/ru/lomov/cloudhood/client/signin_form_view.fxml")));
                contentPane.getChildren().addAll(fxml);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

    }

    public void openSignUpForm() {
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(1), contentPane);
        translateTransition.setToX(0);
        translateTransition.play();
        translateTransition.setOnFinished((e) -> {
            try {
                contentPane.getChildren().removeAll();
                fxml = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/ru/lomov/cloudhood/client/signup_form_view.fxml")));
                contentPane.getChildren().addAll(fxml);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    //--------------------Служебные методы-----------------------

    public void signIn(ActionEvent actionEvent) throws InterruptedException {
        String login = loginLoginField.getText();
        String password = passwordLoginField.getText();
        network.logIn(login, password);
            if (network.isAuthorized()) {
                try {
                    changeWindow(actionEvent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
    }

    public void signUp(ActionEvent actionEvent) throws InterruptedException {
        String nickname = nicknameRegField.getText();
        String login = loginRegField.getText();
        String password = passwordRegField.getText();
        String repeatPassword = passwordRegFieldRe.getText();
        if (password.equals(repeatPassword)) {
            network.SignUp(nickname, login, password);
            try {
                changeWindow(actionEvent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static ClientNetworkNetty getNetwork() {
        return network;
    }

    public void changeWindow(ActionEvent actionEvent) throws Exception {
        Parent newParent = FXMLLoader.load(
                Objects.requireNonNull(getClass().getResource("/ru/lomov/cloudhood/client/cloudhood_view.fxml")));
        Scene mainScene = new Scene(newParent);
        mainScene.setFill(Color.TRANSPARENT);
        Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        window.setScene(mainScene);
        window.getIcons().add(
                new Image(AuthApp.class.getResourceAsStream("/ru/lomov/cloudhood/client/img/Icon.png")));
        window.show();
    }

    public void processChangingPassword(ActionEvent actionEvent) {
        changePasswordPane.setVisible(true);
        changePasswordPane.setManaged(true);
    }

    public void sendUID(ActionEvent actionEvent) {
        String UID = uidField.getText();
        String password = passwordLoginField1.getText();
        network.changePassword(UID, password);
        System.out.println("UID: " + UID);
        changePasswordPane.setVisible(false);
        changePasswordPane.setManaged(false);
    }

    public void closeApp(ActionEvent actionEvent) {
        network.closeConnect();
        Platform.exit();
        System.exit(0);
    }
}
