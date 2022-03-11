package ru.lomov.cloudhood.common.messages;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import lombok.Data;
import ru.lomov.cloudhood.common.Commands;
import ru.lomov.cloudhood.common.Message;

import java.util.ArrayList;
import java.util.List;
@Data
public class UsersListMsg implements Message {
    private final List<String> usersList =  new ArrayList<>();
    private ChannelGroup channels;

    public UsersListMsg(ChannelGroup channels) {
        for (Channel ch: channels) {
            String nickname = ch.metadata().toString();
            usersList.add(nickname);

        }

    }

    @Override
    public Commands getCommand() {
        return Commands.GET_USERS_LIST;
    }
}
