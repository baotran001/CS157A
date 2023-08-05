package com.example.server;
import java.security.SecureRandom;
import java.sql.*;

public class Utility {
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static String generateRandomId(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder randomId = new StringBuilder(length);
        
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            randomId.append(CHARACTERS.charAt(randomIndex));
        }
        
        return randomId.toString();
    }


    public static Connection createSQLConnection() throws SQLException{
        // Load Driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            }
        catch (Exception E) {
            System.err.println("Unable to load driver.");
            E.printStackTrace();
        }

        // Define Connection URL
        String host = "localhost";
        String dbName = "quizmedb";
        int port = 3306;
        String url = "jdbc:mysql://" + host + ":" +
        port + "/" + dbName;

        // Connect to Database
        String username = "root";
        String password = "Bb32003211";
        Connection connection = DriverManager.getConnection(url, username, password);

        return connection;
    }
}
