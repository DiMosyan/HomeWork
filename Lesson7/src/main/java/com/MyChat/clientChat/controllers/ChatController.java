package com.MyChat.clientChat.controllers;

import com.MyChat.clientChat.Apps.AuthApp;
import com.MyChat.clientChat.Apps.ChatApp;
import com.MyChat.clientChat.FileEditor;
import com.MyChat.clientChat.dialogs.Dialogs;
import com.MyChat.command.Command;
import com.MyChat.command.CommandType;
import com.MyChat.command.commands.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatController {

    @FXML
    public Label userNameLabel;

    @FXML
    public ListView<String> userList;

    @FXML
    public Button sendButton;

    @FXML
    public TextField messageField;

    @FXML
    public TextArea messageArea;

    private String name;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private final Map<String, List<String>> entries = new HashMap<>();
    private FileEditor fileEditor;
    private String prefSelectedUser = null;

    private static ChatController INSTANCE;

    public void start(String name, Socket socket, ObjectInputStream in, ObjectOutputStream out) {
        INSTANCE = this;
        setNetworkValues(name, socket, in, out);
        setEntries();
        readMessage();
    }

    private void setNetworkValues(String name, Socket socket, ObjectInputStream in, ObjectOutputStream out) {
        fileEditor = new FileEditor(name);
        this.name = name;
        this.socket = socket;
        this.in = in;
        this.out = out;
    }

    private void setEntries() {
        userNameLabel.setText(name + ":");
        try {
            Command command = (Command) in.readObject();
            if (command.getType() == CommandType.USER_LIST_INIT) {
                UserListInitCommandData data = (UserListInitCommandData) command.getData();
                fileEditor.createUserDir();
                for (String entrant : data.getNamesOfEntries()) {
                    if (!entrant.equals(name)) {
                        entries.put(entrant, fileEditor.getHistoryFromFile(entrant));
                        userList.getItems().add(entrant);
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Class conversion error");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Set entries is wrong");
            e.printStackTrace();
        }
    }

    private void sendCommand(Command command) {
        try {
            out.writeObject(command);
        } catch (IOException e) {
            System.err.println("ChatController_sendMessage");
            e.printStackTrace();
            Dialogs.ErrorType.NET_ERROR.show(Alert.AlertType.ERROR, "Failed to send message");
        }
    }

    private Command readCommand() throws IOException {
        Command command = null;
        try {
            command = (Command) in.readObject();
        } catch (ClassNotFoundException e) {
            System.err.println("Failed to read Command");
            e.printStackTrace();
        }
        return command;
    }

    private void readMessage() {
        Thread readMessageThread = new Thread(() -> {
            while (true) {
                try {
                    if (Thread.currentThread().isInterrupted()) {
                        return;
                    }
                    Command command = readCommand();

                    switch (command.getType()) {
                        case USER_LIST_UPDATE -> updateUserList(command);
                        case PRIVATE_MESSAGE -> updatePrivateMessage(command);
                        case PUBLIC_MESSAGE -> updatePublicMessage(command);
                        case REGISTRATION_OK -> Platform.runLater(() -> {
                            ChatApp.getInstance().getChangeDataStage().close();
                            Dialogs.ErrorType.SUCCESS_REG.show(Alert.AlertType.INFORMATION, "Your data has been successfully changes!");
                        });
                        case CHANGE_NICK_OK -> changeNick(command);
                        case DISCONNECT -> {
                            disconnected();
                            return;
                        }
                        case ERROR -> {
                            ErrorCommandData data = (ErrorCommandData) command.getData();
                            Platform.runLater(() -> Dialogs.ErrorType.REG_ERROR.show(Alert.AlertType.ERROR, data.getErrorMessage()));
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Reading message is wrong");
                    e.printStackTrace();
                    break;
                }
            }
        });
        readMessageThread.setDaemon(true);
        readMessageThread.start();
    }

    private void updateUserList(Command command) {
        UserListUpdateCommandData data = (UserListUpdateCommandData) command.getData();
        if (data.getType() == UserListUpdateCommandData.UserListUpdateType.ADD) {
            if (name.equals(data.getClientsName())) return;
            String updateListMessage = updateUserListMessageBuilder(data.getClientsName(), data.getType());
            entries.put(data.getClientsName(), fileEditor.getHistoryFromFile(data.getClientsName()));
            entries.get(data.getClientsName()).add(updateListMessage);
            fileEditor.addLineToFile(data.getClientsName(), updateListMessage);
            Platform.runLater(() -> {
                userList.getItems().add(data.getClientsName());
                userList.getItems().sort(String::compareTo);
            });
        } else {
            if (data.getType() == UserListUpdateCommandData.UserListUpdateType.DEL) {
                entries.remove(data.getClientsName());
                fileEditor.addLineToFile(data.getClientsName(), updateUserListMessageBuilder(data.getClientsName(), data.getType()));
                Platform.runLater(() -> userList.getItems().remove(data.getClientsName()));
            }
            if (userList.getSelectionModel().getSelectedItem() != null && userList.getSelectionModel().getSelectedItem().equals(data.getClientsName())) {
                messageArea.setText("");
            }
        }
    }

    private String updateUserListMessageBuilder(String name, UserListUpdateCommandData.UserListUpdateType dataType) {
        StringBuilder strBuilder = new StringBuilder(DateFormat.getDateTimeInstance().format(new Date()))
                .append(System.lineSeparator()).append(name);
        if (dataType == UserListUpdateCommandData.UserListUpdateType.ADD) strBuilder.append(" connected.");
        else strBuilder.append(" disconnected.");
        strBuilder.append("\n\n");
        return strBuilder.toString();
    }

    @FXML
    public void sendMessage(ActionEvent actionEvent) {
        String keyOfEntries;
        if (userList.getSelectionModel().getSelectedItem() == null) {
            return;
        }
        keyOfEntries = userList.getSelectionModel().getSelectedItem();
        String message = messageField.getText();
        messageField.setText("");
        if (message.isEmpty()) {
            return;
        }

        String localMessage = String.format("%s%sI: %s%s%s", DateFormat.getDateTimeInstance().format(new Date()),
                System.lineSeparator(), message,System.lineSeparator(), System.lineSeparator());
        entries.get(keyOfEntries).add(localMessage);
        fileEditor.addLineToFile(keyOfEntries, localMessage);
        messageArea.appendText(localMessage);
        if (keyOfEntries.equals("All")) {
            sendPublicMessage(message);
        }
        sendPrivateMessage(message, keyOfEntries);
    }

    private void sendPrivateMessage(String message, String receiver) {
        sendCommand(Command.privateMessageCommand(message, receiver, this.name));
    }

    private void sendPublicMessage(String message) {
        sendCommand(Command.publicMessageCommand(message, this.name));
    }

    private void updatePublicMessage(Command command) {
        PublicMessageCommandData data = (PublicMessageCommandData) command.getData();
        updateMessage("All", data.getSender(), data.getMessage());
    }

    private void updatePrivateMessage(Command command) {
        PrivateMessageCommandData data = (PrivateMessageCommandData) command.getData();
        updateMessage(data.getSender(), data.getSender(), data.getMessage());
    }

    private void updateMessage(String nameOfChat, String sender, String message) {
        String localMessage = String.format("%s\n%s: %s\n\n", DateFormat.getDateTimeInstance().format((new Date())),
                sender, message);
        entries.get(nameOfChat).add(localMessage);
        fileEditor.addLineToFile(nameOfChat, localMessage);
        if (userList.getSelectionModel().getSelectedItem() != null && userList.getSelectionModel().getSelectedItem().equals(nameOfChat)) {
            messageArea.appendText(localMessage);
        } else {
            Platform.runLater(() -> {
                int i = userList.getItems().indexOf(nameOfChat);
                if(i != -1) userList.getItems().set(i, nameOfChat + "*");
            });
        }
    }

    @FXML
    public void shiftMessageArea(MouseEvent mouseEvent) {
        if (userList.getSelectionModel().getSelectedItem() != null && !userList.getSelectionModel().getSelectedItem().equals(prefSelectedUser)) {
            String value = userList.getSelectionModel().getSelectedItem();
            messageArea.setText("");
            messageArea.setPrefRowCount(entries.get(value).size());
            if (value.endsWith("*")) {
                value = value.substring(0, value.length() - 1);
                userList.getItems().set(userList.getSelectionModel().getSelectedIndex(), value);
            }
            for (String line : entries.get(value)) {
                messageArea.appendText(line);
            }
            prefSelectedUser = userList.getSelectionModel().getSelectedItem();
        }
    }

    private void disconnected() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            System.err.println("Closing is wrong");
            e.printStackTrace();
        }
    }

    public void sendExternalCommand(Command command) {
        sendCommand(command);
    }

    public void sendDisconnectCommand(ActionEvent actionEvent) {
        sendCommand(Command.disconnectCommand());
        AuthApp.getInstance().getAuthStage().close();
    }

    public void changeDataInitDialog(String type, String name) throws IOException {
        ChatApp.getInstance().initDialog("ChangeDataPane-view.fxml", type, name);
    }

    public void changeNickInitDialog(ActionEvent actionEvent) {
        try {
            changeDataInitDialog("nick", this.name);
        } catch (IOException e) {
            System.err.println("Initialization dialog for changing nick error.");
            e.printStackTrace();
        }
    }

    public void changeLoginInitDialog(ActionEvent actionEvent) {
        try {
            changeDataInitDialog("login", this.name);
        } catch (IOException e) {
            System.err.println("Initialization dialog for changing login error.");
            e.printStackTrace();
        }
    }

    public void changePasswordInitDialog(ActionEvent actionEvent) {
        try {
            ChatApp.getInstance().initDialog("ChangePasswordPane-view.fxml", "password", this.name);
        } catch (IOException e) {
            System.err.println("Initialization dialog for changing login error.");
            e.printStackTrace();
        }
    }

    private void changeNick(Command command) {
        ChangeNickSucCommandData data = (ChangeNickSucCommandData) command.getData();
        Platform.runLater(() -> {
            if (data.getOldNick().equals(name)) {
                name = data.getNewNick();
                userNameLabel.setText(data.getNewNick());
                ChatApp.getInstance().getChangeDataStage().close();
                Dialogs.ErrorType.SUCCESS_REG.show(Alert.AlertType.INFORMATION, "Your nick has been successfully changes!");
                for (Map.Entry<String, List<String>> entry : entries.entrySet()) {
                    updateMessage(entry.getKey(), "I", String.format("renamed to %s.", data.getNewNick()));
                }
                fileEditor.renameAllFiles(data.getNewNick());
            } else {
                updateMessage("All", data.getOldNick(), String.format("renamed to %s", data.getNewNick()));
                entries.put(data.getNewNick(), entries.get(data.getOldNick()));
                entries.remove(data.getOldNick());
                fileEditor.renameFile(data.getOldNick(), data.getNewNick());
                userList.getItems().add(data.getNewNick());
                userList.getItems().sort(String::compareTo);

                if(userList.getSelectionModel().getSelectedItem() != null &&
                        userList.getSelectionModel().getSelectedItem().equals(data.getOldNick()))
                    userList.getSelectionModel().select(data.getNewNick());

                userList.getItems().remove(data.getOldNick());
                userList.getItems().remove(data.getOldNick() + "*");
                updateMessage(data.getNewNick(), data.getOldNick(), String.format("renamed to %s", data.getNewNick()));
            }
        });
    }

    public static ChatController getInstance() {
        return INSTANCE;
    }
}