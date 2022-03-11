package ru.lomov.cloudhood.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import ru.lomov.cloudhood.common.messages.*;

import java.nio.file.Path;

public class ClientNetworkNetty {
    private static final int PORT = 8788;
    private static final String HOST = "localhost";
    private ObservableList<FileInfo> list;
    private ObservableList<UserInfo> usersList;
    private FirstClientInboundHandler handler;
    private SocketChannel channel;
    private Thread thread;


    public ClientNetworkNetty() {
     thread =   new Thread(() -> {
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(workerGroup)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                channel = socketChannel;
                                socketChannel.pipeline().addLast(
                                        new ObjectEncoder(),
                                        new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                        handler = new FirstClientInboundHandler());
                            }
                        });
                ChannelFuture future = bootstrap.connect(HOST, PORT).sync();
                System.out.println("Соединение найдено..");
                future.channel().closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                workerGroup.shutdownGracefully();
            }
        });
     thread.start();
    }
    public void closeConnect(){
        channel.close();
    }

    public void logIn(String login, String password) {
        channel.writeAndFlush(new LoginRequest(login, password));
    }

    public void SignUp(String nickname, String login, String password) {
        channel.writeAndFlush(new RegistrationRequest(nickname, login, password));
    }
    public void changePassword(String uid, String password){
        channel.writeAndFlush(new PasswordRequest(uid,password));
    }

    public void changeNickname(String oldNickname,String newNickname){
        channel.writeAndFlush(new NicknameRequest(oldNickname,newNickname));
    }

    public void postFile(Path path) {
        channel.writeAndFlush(new FileMsg(path));
    }

    public void refreshFileList(String nickname) {
        channel.writeAndFlush(new ListRequest(nickname));
    }

    public void deleteFile(String fileName) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        channel.writeAndFlush(new DeleteRequest(fileName));
    }

    public void postAva(Path path) {
        channel.writeAndFlush(new AvaMsg(path));
    }

//--------------------------------------------------------------getters--------------

    public void getFile(String fileName) {
        channel.writeAndFlush(new FileRequest(fileName));
    }

    public ObservableList<FileInfo> getList() {
        System.out.println(list = handler.getList());
        return list;
    }

    public ObservableList<UserInfo> getUsersList() {
        System.out.println(usersList = handler.getUsersList());
        return usersList;
    }

    public String getNickname() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return handler.getNickname();
    }

    public boolean isAuthorized() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Status: " + handler.isAuthOk());
            return handler.isAuthOk();
    }
    public long getUID(){return handler.getUID();}



}
