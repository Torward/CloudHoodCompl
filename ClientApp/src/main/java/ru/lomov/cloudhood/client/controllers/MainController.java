package ru.lomov.cloudhood.client.controllers;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.lomov.cloudhood.client.AuthApp;
import ru.lomov.cloudhood.client.ClientNetworkNetty;
import ru.lomov.cloudhood.client.FileInfo;
import ru.lomov.cloudhood.client.UserInfo;

import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class MainController implements Initializable {
    @FXML
    private TextField nicknameChangeField;
    @FXML
    private AnchorPane changeNicknamePane;
    @FXML
    private Button closeBTN;
    @FXML
    private Label alertLabel;
    @FXML
    private TextField uidField;
    @FXML
    private Button userListBTN;
    @FXML
    private AnchorPane userListPane;
    @FXML
    private TableView<UserInfo> userListTableView;
    @FXML
    private TableColumn<UserInfo, String> user;
    @FXML
    private TableView<FileInfo> serverView;
    @FXML
    private TableColumn<FileInfo, String> name;
    @FXML
    private TableColumn<FileInfo, String> size;
    @FXML
    private TableColumn<FileInfo, String> date;
    @FXML
    private TableColumn<FileInfo, ImageView> icon;
    @FXML
    private Button delete_btn;
    @FXML
    private AnchorPane dragAndDropPane;
    @FXML
    private Label text;
    @FXML
    private Button download_btn;
    @FXML
    private Button upload_btn;
    @FXML
    private TextArea log_area;
    @FXML
    private AnchorPane person;
    @FXML
    private ImageView ava;
    @FXML
    private Label nicknameLabel;
    private String nickname;


    private ClientNetworkNetty network;
    private ObservableList<FileInfo> list;
    private ObservableList<UserInfo> usersList;
    private Path techDir;
    private long uid;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        nicknameLabel.setUnderline(true);
        network = AuthFormViewController.getNetwork();
        name.setCellValueFactory(new PropertyValueFactory<FileInfo, String>("name"));
        size.setCellValueFactory(new PropertyValueFactory<FileInfo, String>("size"));
        date.setCellValueFactory(new PropertyValueFactory<FileInfo, String>("date"));
        userListPane.setVisible(false);
        userListPane.setManaged(false);
        setNickname();
        reload();

        System.out.println("Проверка итерации");
    }

    @FXML
    private void changeNickname(MouseEvent mouseEvent) {
        changeNicknamePane.setVisible(true);
        changeNicknamePane.setManaged(true);
    }

    public void sendNewNickname(ActionEvent actionEvent) {
        network.changeNickname(nickname, nicknameChangeField.getText());
        nicknameLabel.setText(network.getNickname());
        changeNicknamePane.setVisible(false);
        changeNicknamePane.setManaged(false);
        printLog(log_area, "Новое имя пользователя установлено.");
    }

    public void setNickname() {
        nickname = network.getNickname();
        nicknameLabel.setText(nickname);
        techDir = Paths.get("ClientApp/src/main/resources/ru/lomov/cloudhood/client/img/" + nickname);
        uid = network.getUID();
        if (uid > 0) {
            uidField.setText(String.valueOf(uid));
        } else {
            uidField.setVisible(false);
            uidField.setManaged(false);
            alertLabel.setVisible(false);
            alertLabel.setManaged(false);
        }
    }

    // Командные методы-----------------------------------------------

    public void dropped(DragEvent dragEvent) {
        List<File> files = dragEvent.getDragboard().getFiles();
        File fileToSend = files.get(0);
        if (fileToSend != null) {
            network.postFile(fileToSend.toPath());
            files = Collections.singletonList(fileToSend);
            printLog(log_area, files);
        }
    }

    public void sendDAnaD(DragEvent dragEvent) {
        if (dragEvent.getDragboard().hasFiles()) {
            dragEvent.acceptTransferModes(TransferMode.ANY);
        }
    }

    public void fileChoose(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        Stage secondaryStage = new Stage();
        fileChooser.setTitle("Выберете файл для отправки");
        fileChooser.setInitialDirectory(new File("C:\\Users\\Gorlum\\Desktop\\Cloud Hood App\\ClientApp\\clientFiles"));
        File fileToSend = fileChooser.showOpenDialog(secondaryStage);
        if (fileToSend != null) {
            network.postFile(fileToSend.toPath());
            List<File> files = Collections.singletonList(fileToSend);
            printLog(log_area, files);
        }

    }

    public void fileChooseM(MouseEvent mouseEvent) {
        FileChooser fileChooser = new FileChooser();
        Stage secondaryStage = new Stage();
        fileChooser.setTitle("Выберете файл для отправки");
        fileChooser.setInitialDirectory(new File("C:\\Users\\Gorlum\\Desktop\\Cloud Hood App\\ClientApp\\clientFiles"));
        File fileToSend = fileChooser.showOpenDialog(secondaryStage);
        if (fileToSend != null) {

            network.postFile(fileToSend.toPath());
            List<File> files = Collections.singletonList(fileToSend);
            printLog(log_area, files);
        }
    }

    public void avaChoose(MouseEvent mouseEvent) {
        FileChooser fileChooser = new FileChooser();
        Stage secondaryStage = new Stage();
        fileChooser.setTitle("Выберете аватар");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png")
        );
        File avaToSend = fileChooser.showOpenDialog(secondaryStage);
        System.out.println("Было выбрано изображение: " + avaToSend.getName());
        if (avaToSend != null) {
            network.postAva(avaToSend.toPath());
            List<File> files = Collections.singletonList(avaToSend);
            printLog(log_area, "Выбрано изображение: "+avaToSend.getName());
        }
        try {
            getAva();
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }

    public void getAva() throws URISyntaxException, IOException {
        Image image = null;
        File[] files = techDir.toFile().listFiles();
        System.out.println(files.length);
        if (files != null) {
            for (File file : files) {
                String path = file.getCanonicalPath();
                System.out.println(path);
                System.out.println(file.toString());
                image = new Image(file.toURI().toString(), 100, 100, false, false);
                ava.setImage(image);
                System.out.println("Имя файла: " + file.getName());
                printLog(log_area, file.getName()+" установлена");
            }

        }
    }

    public void delete(ActionEvent actionEvent) {
        String fileName = serverView.getSelectionModel().getSelectedItem().getName();
        network.deleteFile(fileName);
        printLog(log_area, fileName+" удалён!");
    }

    public void download() throws IOException {
        String fileName = serverView.getSelectionModel().getSelectedItem().getName();
        network.getFile(fileName);
        printLog(log_area, fileName+" загружен");
    }

    public void reload() {
        network.refreshFileList(nickname);
        list = network.getList();
        System.out.println("Element in collection: " + list.size());
        serverView.setItems(list);
        serverView.comparatorProperty();
        printLog(log_area, "Список файлов обновлён.");
    }

//    public void showUserList(ActionEvent actionEvent) {
//        usersList = network.getUsersList();
//        System.out.println("Element in collection: " + usersList.size());
//        userListTableView.setItems(usersList);
//        userListTableView.comparatorProperty();
//        userListPane.setVisible(true);
//        userListPane.setManaged(true);
//
//    }

    public void copyToClipboard(ActionEvent actionEvent) {
        String text = uidField.getText();
        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        Clipboard.getSystemClipboard().setContent(content);
        uidField.setVisible(false);
        uidField.setManaged(false);
        alertLabel.setVisible(false);
        alertLabel.setManaged(false);
        printLog(log_area, "Код "+text+ " скопирован в буфер");
    }

    public void closeApp(ActionEvent actionEvent) throws Exception {
        network.closeConnect();
        Platform.exit();
        System.exit(0);
    }
    //---------------------------------------graphics

    public void hover(MouseEvent mouseEvent) {
        dragAndDropPane.setStyle("-fx-background-color: #ffffff65;");
        text.setStyle("-fx-background-color: #ffffff65;" +
                "-fx-text-fill:black;");
    }

    public void objectFree(MouseEvent mouseEvent) {
        dragAndDropPane.setStyle("-fx-background-color: #4b78bb;");
        text.setStyle("-fx-background-color:#4b78bb;");
    }


    //--------------------------------логгирование

    private void printLog(TextArea textArea, List<File> files) {
        if (files == null || files.isEmpty()) {
            return;
        }
        for (File file : files) {
            textArea.appendText(file.getAbsolutePath() + "\n");
        }
    }

    private void printLog(TextArea textArea, String log) {
        textArea.appendText(log + "\n");
    }


}
