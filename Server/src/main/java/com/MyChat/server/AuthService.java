package com.MyChat.server;

import com.MyChat.command.Command;
import com.MyChat.command.commands.ChangeUserDataCommandData;
import com.MyChat.command.commands.RegCommandData;

import java.sql.*;

public class AuthService {

    private Connection connection;
    
    public Command getNick(String login, String password) {
        try {
            connect();
            return userIdentification(login, password);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return Command.errorCommand("Data base connecting error");
        } finally {
            disconnect();
        }
    }

    public Command resultOfReg(Command request) {
        RegCommandData data = (RegCommandData) request.getData();
        try {
            connect();
            if(isFieldBusy("nick", data.getNick())) return Command.errorCommand("This nick is busy.");
            if(isFieldBusy("login", data.getLogin())) return Command.errorCommand("This login is busy.");
            PreparedStatement ps = connection.prepareStatement("INSERT INTO data_of_entries (nick, login, password) VALUES (?, ?, ?);");
            ps.setString(1, data.getNick());
            ps.setString(2, data.getLogin());
            ps.setString(3, data.getPassword());
            ps.executeUpdate();
            return Command.regOkCommand();
        } catch (SQLException | ClassNotFoundException e) {
            return dataConnectingError(e);
        }
    }

    public Command resultOfChangeData(ChangeUserDataCommandData data) {
        String columnName;

        try {
            connect();
            switch (data.getType()) {
                case NICK -> {
                    columnName = "nick";
                    if(isFieldBusy(columnName, data.getValue())) return Command.errorCommand("This nickname is busy!");
                }
                case LOGIN -> {
                    columnName = "login";
                    if(isOldField(columnName, data.getValue(), data.getCurrentNick())) return Command.errorCommand("The new login is the same as the old one.");
                    if(isFieldBusy(columnName, data.getValue())) return Command.errorCommand("This login is busy!");
                }
                case PASSWORD -> {
                    columnName = "password";
                    if(isOldField(columnName, data.getValue(), data.getCurrentNick())) return Command.errorCommand("The new password is the same as the old one.");
                }
                default -> {
                    return Command.errorCommand("Type of command error.");
                }
            }
            PreparedStatement ps = connection.prepareStatement(
                    String.format("UPDATE data_of_entries SET %s = ? WHERE nick = '%s';", columnName, data.getCurrentNick()));
            ps.setString(1, data.getValue());
            ps.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            return dataConnectingError(e);
        } finally {
            disconnect();
        }

        if(columnName.equals("nick")) return Command.changeNickSucCommand(data.getCurrentNick(), data.getValue());
        else return Command.regOkCommand();
    }

    private void connect() throws SQLException, ClassNotFoundException {
        connection = DriverManager.getConnection("jdbc:sqlite:entries.db");
    }

    private Command userIdentification(String login, String password) throws SQLException {
        String nick = null;
        PreparedStatement ps = connection.prepareStatement("SELECT (nick) FROM data_of_entries WHERE login = ? AND password = ?;");
        ps.setString(1, login);
        ps.setString(2, password);
        ResultSet rs = ps.executeQuery();
        while(rs.next()) {
            nick = rs.getString(1);
        }
        if(nick != null) {
            return Command.authOkCommand(nick);
        } else {
            return Command.errorCommand("Login/password is wrong");
        }
    }

    private boolean isFieldBusy(String columnName, String value) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(String.format("SELECT (%s) FROM data_of_entries WHERE %s = ?", columnName, columnName));
        ps.setString(1, value);
        if(ps.executeQuery().next()) return true;
        return false;
    }

    private boolean isOldField(String columnName, String value, String nick) throws SQLException{
        PreparedStatement ps = connection.prepareStatement(String.format("SELECT (%s) FROM data_of_entries WHERE nick = ?", columnName));
        ps.setString(1, nick);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getString(1).equals(value);
    }

    private void disconnect() {
        try {
            if(connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Database closing error.");
            e.printStackTrace();
        }
    }

    private Command dataConnectingError (Exception e) {
        System.err.println("Data base connecting error.");
        e.printStackTrace();
        return Command.errorCommand("Data base connecting error.");
    }
}
