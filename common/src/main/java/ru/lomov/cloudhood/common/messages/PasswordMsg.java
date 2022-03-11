package ru.lomov.cloudhood.common.messages;

import lombok.Data;
import ru.lomov.cloudhood.common.Commands;
import ru.lomov.cloudhood.common.Message;
@Data
public class PasswordMsg implements Message {
    private final String password;

    public PasswordMsg(String password) {
        this.password = password;
    }

    @Override
    public Commands getCommand() {
        return Commands.CHANGE_PASSWORD;
    }
}
