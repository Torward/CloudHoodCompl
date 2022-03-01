package ru.lomov.cloudhood.common.messages;

import lombok.Data;
import ru.lomov.cloudhood.common.Commands;
import ru.lomov.cloudhood.common.Message;
@Data
public class RegistrationMsg implements Message {
    private final long UID;
    private final String nickname;

    public RegistrationMsg(long UID, String nickname) {
        this.UID = UID;
        this.nickname = nickname;
    }

    @Override
    public Commands getCommand() {
        return Commands.REG_OK;
    }
}
