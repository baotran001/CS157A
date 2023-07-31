package com.example.server;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;

@RestController
@RequestMapping("/quizMeDB")
public class MyController {
    @GetMapping("/createFlashCard")
    public String createFlashCard() throws SQLException{
        // Establish SQL Connection
        Connection connection = createSQLConnection();
        // Statement Execution
        try{
            Statement statement = connection.createStatement();
            String query = "show tables";
            statement.executeQuery(query);
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet!=null){
                while(resultSet.next()){
                    System.out.println(resultSet.getString(1));
            }
        }
        connection.close();
        }
        catch (SQLException E){
            System.out.println("SQLException:" + E.getMessage());
            System.out.println("SQLState:" + E.getSQLState());
            System.out.println("VendorError:" + E.getErrorCode());
        }

        return "success";
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