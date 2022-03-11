package ru.lomov.cloudhood.common.messages;

import lombok.Data;
import ru.lomov.cloudhood.common.Commands;
import ru.lomov.cloudhood.common.Message;
@Data
public class PasswordRequest implements Message {
    private final long UID;
    private final String newPassword;

    public PasswordRequest(String UID, String newPassword1) {
        this.UID = Long.parseLong(UID);
        this.newPassword = newPassword1;
    }

    @Override
    public Commands getCommand() {
        return Commands.CHANGE_PASSWORD;
    }
}
