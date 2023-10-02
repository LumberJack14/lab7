package Utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class UserDatabaseManager {
    private Connection conn;

    public UserDatabaseManager() {
        Properties info = new Properties();
        try {
            info.load(new FileInputStream("db.cfg"));
            this.conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Dragons", info);
        } catch (IOException | SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean checkUser(String username) {
        boolean resp = false;
        try {
            String selectString = """
                    SELECT EXISTS (SELECT 1 FROM "User" WHERE username = ?);
                    """;
            PreparedStatement stat = this.conn.prepareStatement(selectString);
            stat.setString(1, username);
            ResultSet rs = stat.executeQuery();
            rs.next();
            resp = rs.getBoolean(1);
            System.out.println("User " + username + " exists: " + resp);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return resp;
    }

    public boolean verifyUser(String username, String password) {
        boolean resp = false;
        try {
            String selectString = """
                    SELECT (password = ?) AS password_match
                    FROM "User"
                    WHERE username = ?;
                    """;
            PreparedStatement stat = this.conn.prepareStatement(selectString);
            stat.setString(1, Encrypted.encryptPassword(password));
            stat.setString(2, username);
            ResultSet rs = stat.executeQuery();
            rs.next();
            resp = rs.getBoolean(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return resp;
    }

    public int addUser(String username, String password) {
        int rowsAffected = 0;
        try {
            String insertString = """
                    INSERT INTO "User" (username, password)
                    VALUES (?, ?);
                    """;
            PreparedStatement stat = this.conn.prepareStatement(insertString);
            stat.setString(1, username);
            stat.setString(2, Encrypted.encryptPassword(password));
            rowsAffected = stat.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return rowsAffected;
    }
}
