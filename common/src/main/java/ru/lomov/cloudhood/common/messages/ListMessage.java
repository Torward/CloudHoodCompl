package ru.lomov.cloudhood.common.messages;

import lombok.Data;
import ru.lomov.cloudhood.common.Commands;
import ru.lomov.cloudhood.common.Message;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@Data
public class ListMessage implements Message {
    private final List<File> filesList =  new ArrayList<>();
    private Path path;

    public ListMessage(Path path) {
      // path = Paths.get("ServerApp/serverFiles/" + nickname);
        if (path.toFile().isDirectory()) {
            File[] dirFiles = path.toFile().listFiles();
            if (dirFiles != null) {
                for (File file : dirFiles) {
                    if (file.isDirectory()) {
                        filesList.add(file);
                        dirFiles = path.toFile().listFiles();
                        if (dirFiles != null) {
                            filesList.addAll(Arrays.asList(dirFiles));
                        }
                    } else {
                        filesList.add(file);
                    }
                }
            }
        }
    }


    @Override
    public Commands getCommand() {
        return Commands.LIST;
    }
}
