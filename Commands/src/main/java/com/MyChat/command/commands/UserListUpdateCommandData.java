package com.MyChat.command.commands;

import java.io.Serializable;

public class UserListUpdateCommandData implements Serializable {

    public enum UserListUpdateType {
        ADD,
        DEL
    }

    private final String clientsName;
    private final UserListUpdateType type;


    public UserListUpdateCommandData(String clientsName, UserListUpdateType type) {
        this.clientsName = clientsName;
        this.type = type;
    }

    public String getClientsName() {
        return clientsName;
    }

    public UserListUpdateType getType() {
        return type;
    }
}
