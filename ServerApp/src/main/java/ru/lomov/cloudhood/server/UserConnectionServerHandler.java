package ru.lomov.cloudhood.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import lombok.extern.slf4j.Slf4j;
import ru.lomov.cloudhood.common.Message;
import ru.lomov.cloudhood.common.messages.*;

import java.io.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

@Slf4j
public class UserConnectionServerHandler extends SimpleChannelInboundHandler<Message> {
    private final JdbcConnect jdbcConnect = new JdbcConnect();
    private boolean authOk = false;
    final ChannelGroup channels;
    private Path techDir;
    private Path rootDir;

    public UserConnectionServerHandler(ChannelGroup channels) {
        this.channels = channels;
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        try {
            jdbcConnect.connect();
            log.debug("Базу подняли..");
        } finally {
            jdbcConnect.disconnect();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        jdbcConnect.disconnect();
        log.debug("База отключена");
        ctx.deregister();
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) throws Exception {
        switch (message.getCommand()) {
            case USER_REG:
                registerNewUser((RegistrationRequest) message, ctx);
                break;
            case GET_AUTH:
                processUserSignIn((LoginRequest) message, ctx);
                break;
            case CHANGE_PASSWORD:
                changePassword((PasswordRequest) message, ctx);
                break;
            case CHANGE_NICKNAME:
                changeNickname((NicknameRequest) message, ctx);
                break;
            case SHARE:
            case GET_LIST:
            case DELETE:
            case POST_FILE:
            case GET_FILE:
            case GET_USERS_LIST:
            case POST_AVA:
            case GET_AVA:
                pushMessage(message, ctx);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

    //-----------------------------------processors-----------------------

    private void pushMessage(Message message, ChannelHandlerContext ctx) {
        if (authOk) {
            ctx.fireChannelRead(message);
        }
    }

    private void changeNickname(NicknameRequest message, ChannelHandlerContext ctx) {
        String oldNickname = message.getOldNickname();
        String newNickname = message.getNewNickname();
        if (authOk) {
            try {
                jdbcConnect.changeNicknameIfAuth(oldNickname, newNickname);
                rootDir.toFile().renameTo(Path.of("ServerApp/serverFiles/" + newNickname).toFile());
                techDir.toFile().renameTo(Path.of("ServerApp/techFiles/" + newNickname).toFile());
                ctx.writeAndFlush(new NicknameMsg(newNickname));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void changePassword(PasswordRequest message, ChannelHandlerContext ctx) {
        long UID = message.getUID();
        String newPassword = message.getNewPassword();
        System.out.println("Получен код: " + UID + ", получен новый пароль: " + newPassword);
        if (UID != 0 && newPassword != null) {
            try {
                jdbcConnect.changePasswordByUID(UID, newPassword);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void processUserSignIn(LoginRequest message, ChannelHandlerContext ctx) {
        String login = message.getLogin();
        String password = message.getPassword();
        String nickname = authQuery(login, password);
        System.out.println("nickname: " + nickname);
        if (nickname != null) {
            try {
                authOk = true;
                rootDir = Files.createDirectories(Path.of("ServerApp/serverFiles/" + nickname));
                techDir = Files.createDirectories(Path.of("ServerApp/techFiles/" + nickname));
                ctx.pipeline().addLast(new FirstServerInboundHandler(nickname, channels));
                ctx.writeAndFlush(new LoginMsg(nickname));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void registerNewUser(RegistrationRequest message, ChannelHandlerContext ctx) {
        String nickname = message.getNickname();
        String login = message.getLogin();
        String password = message.getPassword();
        if (nickname != null && login != null && password != null) {
            jdbcConnect.createPsUpdate(nickname, login, password);
            try {
                authOk = true;
                ctx.pipeline().addLast(new FirstServerInboundHandler(nickname, channels));
                ctx.writeAndFlush(new RegistrationMsg(jdbcConnect.getUID(), nickname));
                techDir = Files.createDirectories(Path.of("ServerApp/techFiles/" + nickname));
                rootDir = Files.createDirectories(Path.of("ServerApp/serverFiles/" + nickname));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String authQuery(String login, String password) {
        String nickname = jdbcConnect.getNicknameByLoginAndPassword(login, password);
        if (nickname != null) {
            log.debug("Пользователь авторизован! Его зовут: " + nickname);
            return nickname;
        } else {
            return null;
        }
    }
}
