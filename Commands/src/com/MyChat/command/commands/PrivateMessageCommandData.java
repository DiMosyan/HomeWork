package com.MyChat.command.commands;

import java.io.Serializable;

public class PrivateMessageCommandData implements Serializable {

    private final String message;
    private final String receiver;
    private final String sender;

    public PrivateMessageCommandData(String message, String receiver, String sender) {
        this.message = message;
        this.receiver = receiver;
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getSender() {
        return sender;
    }
}
