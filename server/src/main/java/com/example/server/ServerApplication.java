package com.example.server;
import java.sql.*;

import org.apache.logging.log4j.core.config.Configurator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class ServerApplication {

    public static void main(String[] args) throws SQLException {
        
        SpringApplication.run(ServerApplication.class, args);
        // Create database 
        Connection connection = createSQLConnection();
        Statement statement = connection.createStatement();
        String dbName = "quizmedb";
        String query = "CREATE DATABASE IF NOT EXISTS " + dbName;
        statement.executeUpdate(query);
        // Check if tables already exist
        String query1 = "USE " + dbName + ";";
        statement.executeUpdate(query1);
        String query2 = "SHOW TABLES;";
        ResultSet rs = statement.executeQuery(query2);
        if (!rs.next()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("createdb.sql"));
        }
        connection.close();
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
        int port = 3306;
        String url = "jdbc:mysql://" + host + ":" +
        port;

        // Connect to Database
        String username = "root";
        String password = "Bb32003211";
        Connection connection = DriverManager.getConnection(url, username, password);

        return connection;
    }
}
