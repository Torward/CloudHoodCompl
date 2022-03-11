package ru.lomov.cloudhood.common.messages;

import lombok.Data;
import ru.lomov.cloudhood.common.Commands;
import ru.lomov.cloudhood.common.Message;
@Data
public class NicknameRequest implements Message {
    private final String oldNickname;
    private final String newNickname;

    public NicknameRequest(String oldNickname, String newNickname) {
        this.oldNickname = oldNickname;
        this.newNickname = newNickname;
    }

    @Override
    public Commands getCommand() {
        return Commands.CHANGE_NICKNAME;
    }
}
