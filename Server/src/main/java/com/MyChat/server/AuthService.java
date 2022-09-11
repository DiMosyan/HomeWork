package com.MyChat.server;

import com.MyChat.command.Command;
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
                System.out.println("Database is closed");
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
