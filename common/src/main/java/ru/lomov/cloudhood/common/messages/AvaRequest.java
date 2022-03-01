package ru.lomov.cloudhood.common.messages;

import lombok.Data;
import ru.lomov.cloudhood.common.Commands;
import ru.lomov.cloudhood.common.Message;
@Data
public class AvaRequest implements Message {
    private final String nickname;

    @Override
    public Commands getCommand() {
        return Commands.GET_AVA;
    }
}
