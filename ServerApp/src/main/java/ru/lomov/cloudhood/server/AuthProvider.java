package ru.lomov.cloudhood.server;

import java.sql.SQLException;

public interface AuthProvider {
    String getNicknameByLoginAndPassword(String login, String password);
    void changePasswordByUID(long UID, String newPassword) throws SQLException;
    void changeNicknameIfAuth(String oldNickname, String newNickname) throws SQLException;
}
