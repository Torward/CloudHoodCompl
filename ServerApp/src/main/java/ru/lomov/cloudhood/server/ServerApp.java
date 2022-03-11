package ru.lomov.cloudhood.server;

public class ServerApp {

    public static void main(String[] args) {
            try {
                new NettyConnect().run();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}
