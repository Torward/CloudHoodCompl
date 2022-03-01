package ru.lomov.cloudhood.common.messages;

import lombok.Data;
import ru.lomov.cloudhood.common.Commands;
import ru.lomov.cloudhood.common.Message;
@Data
public class LoginRequest implements Message {

    private final String login;
    private final String password;

    public LoginRequest(String login, String password) {
        this.login = login;
        this.password = password;
    }

    @Override
    public Commands getCommand() {
        return Commands.GET_AUTH;
    }
}
