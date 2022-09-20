package com.MyChat.command.commands;

import java.io.Serializable;
import java.util.List;

public class UserListInitCommandData  implements Serializable {
    private final List<String> namesOfEntries;

    public UserListInitCommandData(List<String> namesOfEntries) {
        this.namesOfEntries = namesOfEntries;
    }

    public List<String> getNamesOfEntries() {
        return namesOfEntries;
    }
}
