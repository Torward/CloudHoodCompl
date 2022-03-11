package ru.lomov.cloudhood.common.messages;

import lombok.Data;
import ru.lomov.cloudhood.common.Commands;
import ru.lomov.cloudhood.common.Message;
@Data
public class StringMsg implements Message {
    private final String stringMsg;

    public StringMsg(String stringMsg) {
        this.stringMsg = stringMsg;
    }

    @Override
    public Commands getCommand() {
        return Commands.MESSAGE;
    }
}
