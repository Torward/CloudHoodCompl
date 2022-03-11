package ru.lomov.cloudhood.common.messages;

import lombok.Data;
import ru.lomov.cloudhood.common.Commands;
import ru.lomov.cloudhood.common.Message;
@Data
public class DeleteRequest implements Message {
    private final String fileName;
    @Override
    public Commands getCommand() {
        return Commands.DELETE;
    }
}
