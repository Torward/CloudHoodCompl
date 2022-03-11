package ru.lomov.cloudhood.common.messages;

import lombok.Data;
import ru.lomov.cloudhood.common.Commands;
import ru.lomov.cloudhood.common.Message;

import java.nio.file.Path;
@Data
public class ShareRequest implements Message {
    private final String nickname;
    private final Path path;

    public ShareRequest(String nickname, Path path) {
        this.nickname = nickname;
        this.path = path;
    }

    @Override
    public Commands getCommand() {
        return Commands.SHARE;
    }
}
