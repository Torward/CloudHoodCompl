package ru.lomov.cloudhood.common.messages;

import lombok.Data;
import ru.lomov.cloudhood.common.Commands;
import ru.lomov.cloudhood.common.Message;

@Data
public class FileRequest implements Message {

    private final String fileName;

    @Override
    public Commands getCommand() {
        return Commands.GET_FILE;
    }
}
