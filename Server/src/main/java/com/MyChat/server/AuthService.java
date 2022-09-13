package com.MyChat.server;

import com.MyChat.command.Command;
import com.MyChat.command.commands.RegCommandData;

import java.sql.*;

public class AuthService {

    private Connection connection;
    private Statement stmt;
    
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
            System.err.println("Data base connecting error.");
            e.printStackTrace();
            return Command.errorCommand("Data base connecting error.");
        }
    }

    private void connect() throws SQLException, ClassNotFoundException {
        connection = DriverManager.getConnection("jdbc:sqlite:entries.db");
        stmt = connection.createStatement();
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

    private void disconnect() {
        try {
            if(stmt != null) {
                stmt.close();
            }
        } catch (SQLException e) {
            System.err.println("Statement closing error");
            e.printStackTrace();
        }
        try {
            if(connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Database closing error.");
            e.printStackTrace();
        }
    }

    private void readEx() throws SQLException {
        ResultSet rs = stmt.executeQuery("SELECT * FROM data_of_entries;");
        while(rs.next()) {
            System.out.println(String.format("%d %s %s %s", rs.getInt(1), rs.getString("nick"),
                    rs.getString(3), rs.getString(4)));
        }
    }

    private void insertEx() throws SQLException {
        stmt.executeUpdate("INSERT INTO data_of_entries (nick, login, password) VALUES ('nick4', 'login4', 'pass4')");
    }
}
