package com.example.server;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;

@RestController
@RequestMapping("/quizMeDB")
public class MyController {
    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }

    @GetMapping("/database-info")
    public String getDatabaseInfo() throws SQLException {
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

        // Display Information about the Database
        DatabaseMetaData dbMetaData = connection.getMetaData();
        String productName =
        dbMetaData.getDatabaseProductName();
        System.out.println("Database: " + productName);
        String productVersion =
        dbMetaData.getDatabaseProductVersion();
        System.out.println("Version: " + productVersion);

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
}
