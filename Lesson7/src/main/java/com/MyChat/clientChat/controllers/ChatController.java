package com.MyChat.clientChat.controllers;

import com.MyChat.clientChat.AuthApp;
import com.MyChat.clientChat.ChatApp;
import com.MyChat.clientChat.dialogs.Dialogs;
import com.MyChat.command.Command;
import com.MyChat.command.CommandType;
import com.MyChat.command.commands.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.*;
import java.net.Socket;
import java.text.DateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
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
    private  final Map<String, StringBuilder> entries = new HashMap<>();

    private static ChatController INSTANCE;

    private void setNetworkValues(String name, Socket socket, ObjectInputStream in, ObjectOutputStream out) {
        this.name = name;
        this.socket = socket;
        this.in = in;
        this.out = out;
    }

    public void start(String name, Socket socket, ObjectInputStream in, ObjectOutputStream out) {
        INSTANCE = this;
        setNetworkValues(name, socket, in, out);
        setEntries();
        readMessage();
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

    private void sendPrivateMessage(String message, String receiver) {
        sendCommand(Command.privateMessageCommand(message, receiver, this.name));
    }

    private void sendPublicMessage(String message) {
        sendCommand(Command.publicMessageCommand(message, this.name));
    }

    private void setEntries() {
        userNameLabel.setText(name + ":");
        try {
            Command command = (Command) in.readObject();

            if(command.getType() == CommandType.USER_LIST_INIT) {
                UserListInitCommandData data = (UserListInitCommandData) command.getData();
                for(String entrant : data.getNamesOfEntries()) {
                    if(!entrant.equals(name)) {
                        entries.put(entrant, new StringBuilder());
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

    private void readMessage() {
        Thread readMessageThread = new Thread(new Runnable() {

            @Override
            public void run() {
                while(true) {
                    try {
                        if(Thread.currentThread().isInterrupted()) {
                            return;
                        }
                        Command command = readCommand();

                        switch (command.getType()) {
                            case USER_LIST_UPDATE -> updateUserList(command);
                            case PRIVATE_MESSAGE -> updatePrivateMessage(command);
                            case PUBLIC_MESSAGE -> updatePublicMessage(command);
                            case REGISTRATION_OK -> {
                                Platform.runLater(() -> {
                                    ChatApp.getInstance().getChangeDataStage().close();
                                    Dialogs.ErrorType.SUCCESS_REG.show(Alert.AlertType.INFORMATION, "Your data has been successfully changes!");
                                });
                            }
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
            }
        });
        readMessageThread.setDaemon(true);
        readMessageThread.start();
    }

    private void updateUserList(Command command) {
        UserListUpdateCommandData data = (UserListUpdateCommandData) command.getData();
        if(data.getType() == UserListUpdateCommandData.UserListUpdateType.ADD) {
            if(name.equals(data.getClientsName())) return;

            for (Map.Entry<String, StringBuilder> pair : entries.entrySet()) {
                if (data.getClientsName().equals(pair.getKey())) {
                    entries.replace(pair.getKey(), pair.getValue().append(DateFormat.getDateInstance().format(new Date()))
                            .append(System.lineSeparator()).append(pair.getKey()).append(" connected").append(System.lineSeparator())
                            .append(System.lineSeparator()));
                    if(userList.getSelectionModel().getSelectedItem() != null && userList.getSelectionModel().getSelectedItem().equals(pair.getKey())) {
                        messageArea.setText(entries.get(pair.getKey()).toString());
                    }
                    return;
                }
            }
            
            entries.put(data.getClientsName(), new StringBuilder(data.getClientsName()).append(" connected").append(System.lineSeparator())
                    .append(System.lineSeparator()));
            Platform.runLater(() -> userList.getItems().add(data.getClientsName()));
        } else {
            if(data.getType() == UserListUpdateCommandData.UserListUpdateType.DEL) {
                entries.remove(data.getClientsName());
                Platform.runLater(() -> {userList.getItems().remove(data.getClientsName());});
            }
            if(userList.getSelectionModel().getSelectedItem() != null && userList.getSelectionModel().getSelectedItem().equals(data.getClientsName())) {
                messageArea.setText("");
            }
        }
        Platform.runLater(() -> userList.getItems().sort((o1, o2) -> o1.compareTo(o2)));
    }

    @FXML
    public void sendPrivateMessage(ActionEvent actionEvent) {
        String keyOfEntries;

        if(userList.getSelectionModel().getSelectedItem() == null) {
            return;
        }

        keyOfEntries = userList.getSelectionModel().getSelectedItem();
        String message = messageField.getText();
        messageField.setText("");
        if(message.isEmpty()) {
            return;
        }
        entries.replace(keyOfEntries, entries.get(keyOfEntries).append(DateFormat.getDateInstance().format(new Date())).append(System.lineSeparator())
                .append("I").append(": ").append(message).append(System.lineSeparator()).append(System.lineSeparator()));
        messageArea.setText(entries.get(keyOfEntries).toString());

        if(keyOfEntries.equals("All")) {
            sendPublicMessage(message);
        }

        sendPrivateMessage(message, keyOfEntries);
    }

    private void updatePublicMessage(Command command) {
        PublicMessageCommandData data = (PublicMessageCommandData) command.getData();
        entries.put("All", entries.get("All").append(DateFormat.getDateInstance().format((new Date()))))
                .append(System.lineSeparator()).append(data.getSender() + ": " + data.getMessage()).append(System.lineSeparator()).append(System.lineSeparator());
        if(userList.getSelectionModel().getSelectedItem() != null && userList.getSelectionModel().getSelectedItem().equals("All")) {
            messageArea.setText(entries.get("All").toString());
        } else {
            Platform.runLater(() -> userList.getItems().set(userList.getItems().indexOf("All"), "All*"));
        }
    }

    private void updatePrivateMessage(Command command) {
        PrivateMessageCommandData data = (PrivateMessageCommandData) command.getData();
        entries.put(data.getSender(), entries.get(data.getSender()).append(DateFormat.getDateInstance().format(new Date())))
                .append(System.lineSeparator()).append(data.getSender() + ": " + data.getMessage()).append(System.lineSeparator()).append(System.lineSeparator());
        if(userList.getSelectionModel().getSelectedItem() != null && userList.getSelectionModel().getSelectedItem().equals(data.getSender())) {
            messageArea.setText(entries.get(data.getSender()).toString());
        } else {
            Platform.runLater(() -> userList.getItems().set(userList.getItems().indexOf(data.getSender()), data.getSender() + "*"));
        }
    }

    @FXML
    public void shiftMessageArea(MouseEvent mouseEvent) {
        if(userList.getSelectionModel().getSelectedItem() != null) {
            String value = userList.getSelectionModel().getSelectedItem();
            if(value.endsWith("*")) {
                value = value.substring(0, value.length() - 1);
                userList.getItems().set(userList.getSelectionModel().getSelectedIndex(), value);
            }
            messageArea.setText(entries.get(userList.getSelectionModel().getSelectedItem()).toString());
        }
    }

    private void disconnected() {
        try{
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

    public void changeDataInitDialog(String type, String name) throws IOException{
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
            if(data.getOldNick().equals(name)) {
                name = data.getNewNick();
                userNameLabel.setText(data.getNewNick());
                ChatApp.getInstance().getChangeDataStage().close();
                Dialogs.ErrorType.SUCCESS_REG.show(Alert.AlertType.INFORMATION, "Your nick has been successfully changes!");
            } else {
                entries.put(data.getNewNick(), entries.get(data.getOldNick()).append(DateFormat.getDateInstance().format(new Date()))
                        .append(System.lineSeparator()).append(data.getOldNick() + " renamed to " + data.getNewNick()).append(System.lineSeparator())
                        .append(System.lineSeparator()));
                entries.remove(data.getOldNick());
                entries.replace("All", entries.get("All").append(DateFormat.getDateInstance().format(new Date()))
                        .append(System.lineSeparator()).append(data.getOldNick() + " renamed to " + data.getNewNick()).append(System.lineSeparator())
                        .append(System.lineSeparator()));
                if(userList.getSelectionModel().getSelectedItem() != null && userList.getSelectionModel().getSelectedItem().equals(data.getOldNick())) {
                    messageArea.setText(entries.get(data.getNewNick()).toString());
                } else if(userList.getSelectionModel().getSelectedItem() != null && userList.getSelectionModel().getSelectedItem().
                        equals("All")) messageArea.setText(entries.get("All").toString());
                userList.getItems().remove(data.getOldNick());
                userList.getItems().add(data.getNewNick());
            }
        });

    }

    public static ChatController getInstance() {
        return INSTANCE;
    }
}