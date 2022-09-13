package com.MyChat.command;

import com.MyChat.command.commands.*;

import java.io.Serializable;
import java.util.List;

public class Command implements Serializable {

    private Object data;
    private CommandType type;

    public static Command authCommand(String login, String password) {
        Command command = new Command();
        command.data = new AuthCommandData(login, password);
        command.type = CommandType.AUTH;
        return command;
    }

    public static Command authOkCommand(String userName) {
        Command command = new Command();
        command.data = new AuthOkCommandData(userName);
        command.type = CommandType.AUTH_OK;
        return command;
    }

    public static Command errorCommand(String errorMessage) {
        Command command = new Command();
        command.data = new ErrorCommandData(errorMessage);
        command.type = CommandType.ERROR;
        return command;
    }

    public static Command privateMessageCommand(String message, String receiver, String sender) {
        Command command = new Command();
        command.data = new PrivateMessageCommandData(message, receiver, sender);
        command.type = CommandType.PRIVATE_MESSAGE;
        return command;
    }

    public static Command publicMessageCommand(String message, String sender) {
        Command command = new Command();
        command.data = new PublicMessageCommandData(message, sender);
        command.type = CommandType.PUBLIC_MESSAGE;
        return command;
    }

    public static Command userListUpdateCommand(String clientsName, UserListUpdateCommandData.UserListUpdateType updateType) {
        Command command = new Command();
        command.data = new UserListUpdateCommandData(clientsName, updateType);
        command.type = CommandType.USER_LIST_UPDATE;
        return command;
    }

    public static Command userListInitCommand(List<String> clientsNames) {
        Command command = new Command();
        command.data = new UserListInitCommandData(clientsNames);
        command.type = CommandType.USER_LIST_INIT;
        return command;
    }

    public static Command disconnectCommand() {
        Command command = new Command();
        command.data = null;
        command.type = CommandType.DISCONNECT;
        return command;
    }

    public static Command regCommand(String nick, String login, String password) {
        Command command = new Command();
        command.data = new RegCommandData(nick, login, password);
        command.type = CommandType.REGISTRATION;
        return command;
    }

    public static Command regOkCommand() {
        Command command = new Command();
        command.data = new RegOkCommandData();
        command.type = CommandType.REGISTRATION_OK;
        return command;
    }

    public Object getData() {
        return data;
    }

    public CommandType getType() {
        return type;
    }
}
