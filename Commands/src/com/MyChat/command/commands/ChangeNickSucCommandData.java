package com.MyChat.command.commands;

import java.io.Serializable;

public class ChangeNickSucCommandData implements Serializable {
    private final String oldNick;
    private final String newNick;

    public ChangeNickSucCommandData(String oldNick, String newNick) {
        this.oldNick = oldNick;
        this.newNick = newNick;
    }

    public String getOldNick() {
        return oldNick;
    }

    public String getNewNick() {
        return newNick;
    }
}
