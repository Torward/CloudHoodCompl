package ru.lomov.cloudhood.common;

import java.io.Serializable;

public interface Message extends Serializable {
    Commands getCommand();
}
