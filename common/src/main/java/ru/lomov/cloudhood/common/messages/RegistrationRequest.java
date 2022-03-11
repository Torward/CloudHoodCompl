package ru.lomov.cloudhood.common.messages;

import lombok.Data;
import ru.lomov.cloudhood.common.Commands;
import ru.lomov.cloudhood.common.Message;
@Data
public class RegistrationRequest implements Message {

    private final String nickname;
    private final String login;
    private final String password;

    public RegistrationRequest(String nickname, String login, String password) {
        this.nickname = nickname;
        this.login = login;
        this.password = password;
    }

    @Override
    public Commands getCommand() {
        return Commands.USER_REG;
    }
}
