package ru.lomov.cloudhood.common.messages;

import lombok.Data;
import ru.lomov.cloudhood.common.Commands;
import ru.lomov.cloudhood.common.Message;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Data
public class AvaMsg implements Message {
    private String msgAvaName;
    private long fileSize;
    private byte[] bytes;

    public AvaMsg(Path path) {
        try {
            msgAvaName = path.getFileName().toString();
            fileSize = Files.size(path);
            bytes = Files.readAllBytes(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Commands getCommand() {
        return Commands.POST_AVA;
    }
}
