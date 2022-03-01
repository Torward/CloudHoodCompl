package ru.lomov.cloudhood.common.messages;


import io.netty.channel.DefaultFileRegion;
import io.netty.channel.FileRegion;
import lombok.Data;
import ru.lomov.cloudhood.common.Commands;
import ru.lomov.cloudhood.common.Message;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
@Data
public class FileMsg implements Message {

    public enum MsgType {
        DIR("##dir"), FILE("##file");
        private String type;

        public String getType() {
            return type;
        }

        MsgType(String type) {
            this.type = type;
        }
    }

    private  String msgFileName;
    private  MsgType msgType;
    private long fileSize;
    private byte[] bytes;

    public FileMsg(Path path) {
        try {
            msgFileName = path.getFileName().toString();
            fileSize = Files.size(path);
            msgType = Files.isDirectory(path) ? MsgType.DIR : MsgType.FILE;
            if (msgType.equals(MsgType.DIR)) {
                fileSize = -1L;
            } else {
                bytes = Files.readAllBytes(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Commands getCommand() {
        return Commands.POST_FILE;
    }

}
