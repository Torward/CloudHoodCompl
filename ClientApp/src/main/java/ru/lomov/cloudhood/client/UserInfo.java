package ru.lomov.cloudhood.client;

import javafx.beans.property.SimpleStringProperty;

public class UserInfo {

    private SimpleStringProperty nickname;

    public UserInfo(String nickname) {
        super();
        this.nickname =new SimpleStringProperty(nickname);

    }
    public UserInfo() {}

    public String getNickname() {
        return nickname.get();
    }

    public SimpleStringProperty nicknameProperty() {
        return nickname;
    }
}
