package ru.lomov.cloudhood.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import lombok.extern.slf4j.Slf4j;
import ru.lomov.cloudhood.common.Message;
import ru.lomov.cloudhood.common.messages.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class FirstServerInboundHandler extends SimpleChannelInboundHandler<Message> {
    private String nickname;
    private Path rootDir;
    private Path techDir;
    final ChannelGroup channels;

    public FirstServerInboundHandler(String nickname, ChannelGroup channels) throws IOException {
        this.channels = channels;
        this.nickname = nickname;
        rootDir = Paths.get("ServerApp/serverFiles/" + nickname);
        techDir = Paths.get("ServerApp/techFiles/" + nickname);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("Соединение сервера с клиентом установлено..");
        channels.add(ctx.channel());
        getFileList(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        switch (msg.getCommand()) {
            case GET_FILE:
                getFileToClient((FileRequest) msg, ctx);
                break;
            case POST_FILE:
                postFileToCloud((FileMsg) msg);
                getFileList(ctx);
                break;
            case GET_LIST:
                getFileList(ctx);
                break;
            case DELETE:
                deleteFileFromCloud((DeleteRequest) msg);
                getFileList(ctx);
                break;
//            case SHARE:
//                shareFileRequest((ShareRequest) msg);
//                break;
            case GET_USERS_LIST:
                getUsersList(ctx);
                break;
            case POST_AVA:
                postAvaToCloud((AvaMsg) msg, ctx);
                break;
            case GET_AVA:
                getAva((AvaRequest)msg,ctx);
                break;
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Клиент упал...");
        super.channelInactive(ctx);
        channels.remove(ctx.channel());
        ctx.deregister();
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    //--------------------------------processors----------------------

    private void getAva(AvaRequest msg, ChannelHandlerContext ctx) {
        File[] files = techDir.toFile().listFiles();
        for (File file: files) {
            System.out.println("В служебной директории находится изображение: "+file.toString());
            ctx.writeAndFlush(new AvaMsg(file.toPath()));
        }
    }

    private void postAvaToCloud(AvaMsg msg, ChannelHandlerContext ctx) {
        System.out.println("Получено изображение: "+ msg.getMsgAvaName());
        try {
            Files.write(techDir.resolve(msg.getMsgAvaName()), msg.getBytes());
            Path path = techDir.resolve(msg.getMsgAvaName());
            ctx.writeAndFlush(new AvaMsg(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getUsersList(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(new UsersListMsg(channels));
    }

    private void postFileToCloud(FileMsg msg) {
        try {
            Files.write(rootDir.resolve(msg.getMsgFileName()), msg.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private void shareFileRequest(ShareRequest shareRequest) {
//        String nicknameInterviewer = shareRequest.getNickname();
//        Path pathToGetFile = shareRequest.getPath();
//    }

    private void deleteFileFromCloud(DeleteRequest deleteRequest) {
        Path path = rootDir.resolve(deleteRequest.getFileName());
        if (path.toFile().exists()){
            try {
                Files.delete(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void getFileList(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(new ListMessage(rootDir));
    }

    private void getFileToClient(FileRequest fileRequest, ChannelHandlerContext ctx) {
        Path path = rootDir.resolve(fileRequest.getFileName());
        ctx.writeAndFlush(new FileMsg(path));
    }
}
