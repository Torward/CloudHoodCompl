package ru.lomov.cloudhood.common.messages;

import lombok.Data;
import ru.lomov.cloudhood.common.Commands;
import ru.lomov.cloudhood.common.Message;
@Data
public class NicknameMsg implements Message {
    private final String newNickname;

    public NicknameMsg(String newNickname) {
        this.newNickname = newNickname;
    }

    @Override
    public Commands getCommand() {
        return Commands.NICKNAME;
    }
}
