package com.MyChat.command.commands;

import java.io.Serializable;

public class PublicMessageCommandData implements Serializable {

    private final String message;
    private final String sender;

    public PublicMessageCommandData(String message, String sender) {
        this.message = message;
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }
}
