package ru.lomov.cloudhood.common.messages;

import lombok.Data;
import ru.lomov.cloudhood.common.Commands;
import ru.lomov.cloudhood.common.Message;
@Data
public class LoginMsg implements Message {
    private final String nickname;

    public LoginMsg(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public Commands getCommand() {
        return Commands.AUTH_OK;
    }
}
