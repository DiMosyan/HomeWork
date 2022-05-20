package com.example.lesson7;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatController {

    @FXML
    public Label userNameLabel;

    @FXML
    public ListView userList;

    @FXML
    public Button sendButton;

    @FXML
    public TextField messageField;

    @FXML
    public TextArea messageArea;

    private String name;
    private DataInputStream in;
    private DataOutputStream out;
    private Map<String, StringBuilder> entries = new HashMap<>();

    private void setNetworkValues(String name, DataInputStream in, DataOutputStream out) {
        this.name = name;
        this.in = in;
        this.out = out;
    }

    public void start(String name, DataInputStream in, DataOutputStream out) {
        setNetworkValues(name, in, out);
        setEntries();
        readMessage();
    }

    private void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            System.err.println("Send message is wrong");
            e.printStackTrace();
        }
    }

    private void setEntries() {
        userNameLabel.setText(name + ":");
        try {
            String entriesString = in.readUTF();
            if(entriesString.startsWith("/entries")) {
                String[] entriesArray = entriesString.split(" ");
                for (int i = 1; i < entriesArray.length; i++) {
                    if(!entriesArray[i].equals(name)) {
                        entries.put(entriesArray[i], new StringBuilder(""));
                        userList.getItems().add(entriesArray[i]);
                    }
                }
            } else {
                System.out.println("Verification is still needed here");
            }
        } catch (IOException e) {
            System.err.println("Set entries is wrong");
            e.printStackTrace();
        }
    }


    @FXML
    public void sendMessage(ActionEvent actionEvent) {
        String keyOfEntries;

        if(userList.getSelectionModel().getSelectedItem() == null) {
            return;
        }

        keyOfEntries = userList.getSelectionModel().getSelectedItem().toString();
        String message = messageField.getText();
        messageField.setText("");
        if(message.isEmpty()) {
            return;
        }
        entries.replace(keyOfEntries, entries.get(keyOfEntries).append(DateFormat.getDateInstance().format(new Date())).append(System.lineSeparator())
                .append(name).append(": ").append(message).append(System.lineSeparator()).append(System.lineSeparator()));
        messageArea.setText(entries.get(keyOfEntries).toString());

        sendMessage(String.format("%s %s: %s", keyOfEntries, name, message));
    }

    private void readMessage() {
        Thread readMessageThread = new Thread(new Runnable() {
            String message;
            String[] partsOfMessage;

            @Override
            public void run() {
                try {
                    while(true) {
                        message = in.readUTF();

                        if(message.equals("/end")) {
                            break;
                        }

                        if(message.startsWith("/con")) {
                            partsOfMessage = message.split(" ", 3);
                            if(partsOfMessage[1].equals(name)) continue;
                            entries.put(partsOfMessage[1], new StringBuilder(partsOfMessage[1]).append(" ").append(partsOfMessage[2]));

                            // При подключении 3-го пользователя в консоль выводится ошибка IllegalStateException (обращение не из потока приложения)
                            // К сожалению, я так и не придумал, что с этим делать. Извините
                            userList.getItems().add(partsOfMessage[1]);
                            userList.refresh();
                        } else {
                            partsOfMessage = message.split(":", 2);
                            entries.put(partsOfMessage[0], entries.get(partsOfMessage[0]).append(DateFormat.getDateInstance().format(new Date()))
                                    .append(System.lineSeparator()).append(message).append(System.lineSeparator()).append(System.lineSeparator()));
                            if(userList.getSelectionModel().getSelectedItem() != null && userList.getSelectionModel().getSelectedItem().toString().equals(partsOfMessage[0])) {
                                messageArea.setText(entries.get(partsOfMessage[0]).toString());
                            }
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Reading message is wrong");
                    e.printStackTrace();
                }

            }
        });
        readMessageThread.setDaemon(true);
        readMessageThread.start();
    }

    @FXML
    public void shiftMessageArea(MouseEvent mouseEvent) {
        messageArea.setText(entries.get(userList.getSelectionModel().getSelectedItem().toString()).toString());
    }

    public void disconnected() {
        sendMessage("/end");
        try{
            in.close();
            out.close();
        } catch (IOException e) {
            System.err.println("Closing is wrong");
            e.printStackTrace();
        }
    }
}