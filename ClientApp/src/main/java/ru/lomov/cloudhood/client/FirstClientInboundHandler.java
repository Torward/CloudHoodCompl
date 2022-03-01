package ru.lomov.cloudhood.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import lombok.extern.slf4j.Slf4j;
import ru.lomov.cloudhood.common.Message;
import ru.lomov.cloudhood.common.messages.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.List;

@Slf4j
public class FirstClientInboundHandler extends SimpleChannelInboundHandler<Message> {

    private static ObservableList<FileInfo> list = FXCollections.observableArrayList();
//    private static ObservableList<UserInfo> users = FXCollections.observableArrayList();
    private TableColumn<FileInfo, String> name;
    private TableColumn<FileInfo, String> date;
    private TableColumn<FileInfo, String> size;
    private TableView<FileInfo> serverView;
    private String fileName;
    private String nickname;
    private long UID;
    private boolean authOk = false;
    private String textMsg;
    private static ChannelHandlerContext ctx;
    private Path rootDir;
    private Path techDir;
    private Image img = null;
    private String extension;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("Соединение с сервером установленно..");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        switch (msg.getCommand()) {
            case LIST:
                refreshFileList((ListMessage) msg);
                break;
            case POST_FILE:
                download((FileMsg) msg);
                break;
//            case GET_USERS_LIST:
//                refreshUsersList((UsersListMsg) msg);
//                break;
            case AUTH_OK:
                processLogIn((LoginMsg) msg);
                break;
            case REG_OK:
                processSignUp((RegistrationMsg) msg);
                break;
//            case MESSAGE:
//                processStringMsg((StringMsg) msg);
//                break;
            case POST_AVA:
                postImgToAvatar((AvaMsg)msg);
                break;
            case NICKNAME:
                postNewNickname((NicknameMsg)msg);
                break;
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        textMsg = "Связь с сервером прекращена!";
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    //--------------------processors-----------------------

//    private void processStringMsg(StringMsg msg) {
//        textMsg = msg.getStringMsg();
//        if (textMsg.equals("Войдите в профиль!")) {
//            authOk = false;
//        }
//    }

    private void postNewNickname(NicknameMsg msg) {
        nickname = msg.getNewNickname();
        rootDir.toFile().renameTo(Path.of("ServerApp/serverFiles/" + msg.getNewNickname()).toFile());
        techDir.toFile().renameTo(Path.of("ServerApp/techFiles/" + msg.getNewNickname()).toFile());
    }

    public String postImgToAvatar(AvaMsg msg) {
        int i = msg.getMsgAvaName().lastIndexOf(".");
        extension = msg.getMsgAvaName().substring(i+1);
        try {
            Files.write(techDir.resolve("avatar." + extension), msg.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return extension;
    }


    private void processSignUp(RegistrationMsg msg) {
        UID = msg.getUID();
        nickname = msg.getNickname();
        authOk = true;
        try {
            Files.createDirectories(Path.of("ClientApp/clientFiles/" + nickname));
            Files.createDirectories(Path.of("ClientApp/src/main/resources/ru/lomov/cloudhood/client/img/" + nickname));
        } catch (IOException e) {
            e.printStackTrace();
        }
        rootDir = Paths.get("ClientApp/clientFiles/" + nickname);
        techDir = Paths.get("ClientApp/src/main/resources/ru/lomov/cloudhood/client/img/" + nickname);
    }

    private void processLogIn(LoginMsg msg) {
        nickname = msg.getNickname();
        if (nickname != null ) {
            authOk = true;
            try {
                Files.createDirectories(Path.of("ClientApp/clientFiles/" + nickname));
                Files.createDirectories(Path.of("ClientApp/src/main/resources/ru/lomov/cloudhood/client/img/" + nickname));
            } catch (IOException e) {
                e.printStackTrace();
            }
            rootDir = Paths.get("ClientApp/clientFiles/" + nickname);
            techDir = Paths.get("ClientApp/src/main/resources/ru/lomov/cloudhood/client/img/" + nickname);
        } else {
            authOk = false;
        }
    }

//    private void refreshUsersList(UsersListMsg msg) {
//        ChannelGroup channels = msg.getChannels();
//        for (Channel channel: channels) {
//            System.out.println(channel.metadata());
//        }
//        List<String> usersList = msg.getUsersList();
//        int usersCount = msg.getUsersList().size();
//        System.out.println(usersCount + " пользователей в сети.");
//        byte[] nicknameInBytes;
//        String nicknameOfList;
//        for (String user : usersList) {
//            nicknameInBytes = user.getBytes(StandardCharsets.UTF_8);
//            nicknameOfList = new String(nicknameInBytes);
//            users.add(new UserInfo(nicknameOfList));
//        }
//    }

    private void download(FileMsg msg) throws IOException {
        try {
            Files.write(rootDir.resolve(msg.getMsgFileName()), msg.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void refreshFileList(ListMessage msg) {
        if (!list.isEmpty()) {
            list.clear();
        }
        List<File> filesList = msg.getFilesList();
        int fileCount = msg.getFilesList().size();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
        String fileNameOfList;
        long fileSizeInByte;
        String fileDateOfList;
        String fileSizeOfList = null;
        System.out.println("Получено " + fileCount + " файлов");
        for (File file : filesList) {
            fileNameOfList = file.getName();
            fileSizeInByte = file.length();
            fileDateOfList = sdf.format(file.lastModified());
            if (fileSizeInByte == -1) {
                fileSizeOfList = "папка";
            } else if (fileSizeInByte < (1024)) {
                fileSizeOfList = fileSizeInByte + " B";
            } else if (fileSizeInByte >= (1024) && fileSizeInByte < (1024 * 1024)) {
                long sizeInKb = fileSizeInByte / 1024;
                fileSizeOfList = sizeInKb + " KB";
            } else if (fileSizeInByte >= (1024 * 1024) && fileSizeInByte < (1024 * 1024 * 1024)) {
                long sizeInMb = fileSizeInByte / (1024 * 1024);
                fileSizeOfList = sizeInMb + " MB";
            } else if (fileSizeInByte >= (1024 * 1024 * 1024)) {
                long sizeInGb = fileSizeInByte / (1024 * 1024 * 1024);
                fileSizeOfList = sizeInGb + " GB";
            }
            list.add(new FileInfo(fileNameOfList, fileSizeOfList, fileDateOfList));
        }
        System.out.println("Количество файлов в списке "+list.size());
    }

    //-------------------------getters--------------------------------------

    public String getNickname() {
        return nickname;
    }

    public String getTextMsg() {
        return textMsg;
    }

    public long getUID() {
        return UID;
    }

    public ObservableList<FileInfo> getList() {
        return list;
    }

//    public ObservableList<UserInfo> getUsersList() {
//        return users;
//    }

    public boolean isAuthOk() {
        return authOk;
    }
}
