package ru.lomov.cloudhood.server;

import java.nio.charset.StandardCharsets;
import java.sql.*;

public class JdbcConnect implements AuthProvider {
    private Connection connection;
    private Statement statement;
    private PreparedStatement preparedStatement = null;
    private long UID;

    private static final String CREATE_NEW_USER = "insert into users (uid, nickname, login, password) values (?, ?, ?, ?);";
    private static final String CREATE_NEW_PASSWORD = "update users set PASSWORD = ? where UID = ?;";
    private static final String CREATE_NEW_NICKNAME = "update users set NICKNAME = ? where NICKNAME = ?;";


    public void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:cloud_hood_db.db");
            statement = connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Подключение к базе данных провалено!");
        }
    }

    public void disconnect() {
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void createTable() {
        try {
            statement.executeUpdate("CREATE TABLE if not exists users (\n" +
                    " UID TEXT PRIMARY KEY UNIQUE, \n" +
                    "nickname TEXT UNIQUE, \n" +
                    "login TEXT UNIQUE, \n" +
                    "password TEXT\n" +
                    ");;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createPsUpdate(String nickname, String login, String password) {

        UID = setUID(nickname, login, password);
        connect();
        createTable();
        try {
            preparedStatement = connection.prepareStatement(CREATE_NEW_USER);
            preparedStatement.setLong(1, UID);
            preparedStatement.setString(2, nickname);
            preparedStatement.setString(3, login);
            preparedStatement.setString(4, password);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public long setUID(String nickname, String login, String password) {
        long UID = 0;
        byte[] partOne = nickname.getBytes(StandardCharsets.UTF_8);
        byte[] partTwo = login.getBytes(StandardCharsets.UTF_8);
        byte[] partThree = password.getBytes(StandardCharsets.UTF_8);
        for (int i = 0; i < partOne.length; i++) {
            for (int j = 0; j < partTwo.length; j++) {
                for (int k = 0; k < partThree.length; k++) {
                    UID += (partOne[i] + partTwo[j] + partThree[k]) * System.currentTimeMillis();
                    if (UID < 0) {
                        UID *= -1;
                    }
                }
            }
        }
        return UID;
    }

    public long getUID() {
        return UID;
    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        connect();
        try (ResultSet rs = statement.executeQuery("select nickname from users where login = '" + login + "' and password = '" + password + "';")) {
            System.out.println(rs.getMetaData());
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void changePasswordByUID(long UID, String newPassword) throws SQLException {
        connect();
        preparedStatement = connection.prepareStatement(CREATE_NEW_PASSWORD);
        preparedStatement.setString(1, newPassword);
        preparedStatement.setLong(2, UID);
        preparedStatement.executeUpdate();
    }

    @Override
    public void changeNicknameIfAuth(String oldNickname, String newNickname) throws SQLException {
        preparedStatement = connection.prepareStatement(CREATE_NEW_NICKNAME);
        preparedStatement.setString(1, newNickname);
        preparedStatement.setString(2, oldNickname);
        preparedStatement.executeUpdate();
    }
}
