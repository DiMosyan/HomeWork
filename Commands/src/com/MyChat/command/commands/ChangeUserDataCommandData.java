package com.MyChat.command.commands;

import java.io.Serializable;

public class ChangeUserDataCommandData implements Serializable {
    public enum TypeOfData {
        NICK("nick"),
        LOGIN("login"),
        PASSWORD("password");

        private final String type;

        TypeOfData(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }

    private final TypeOfData type;
    private final String value;
    private final String currentNick;


    public ChangeUserDataCommandData(TypeOfData type, String values, String nick) {
        this.type = type;
        this.value = values;
        this.currentNick = nick;
    }

    public TypeOfData getType() {
        return this.type;
    }

    public String getValue() {
        return value;
    }

    public String getCurrentNick() {
        return currentNick;
    }
}
