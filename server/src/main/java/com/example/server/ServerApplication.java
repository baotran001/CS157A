package com.example.server;
import java.sql.*;

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

        //hi
        String insertQuery = "INSERT INTO Users (uid, password) VALUES ('hi', 'hi')";
        PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
        insertStatement.executeUpdate();

        //folder one
        insertQuery = "INSERT INTO Folder (fid, name, author, description) VALUES ('samplefolderone', 'sample math folder', 'hi', 'learning math is fun!')";
        insertStatement = connection.prepareStatement(insertQuery);
        insertStatement.executeUpdate();

        insertQuery = "INSERT INTO UsersCreatesFolder (uid, fid) VALUES ('hi', 'samplefolderone')";
        insertStatement = connection.prepareStatement(insertQuery);
        insertStatement.executeUpdate();

        //set one
        insertQuery = "INSERT INTO Sets (sid, name, author, date, description) VALUES ('samplesetone', 'basic math', 'hi', '2023-08-08', 'elementary math!')";
        insertStatement = connection.prepareStatement(insertQuery);
        insertStatement.executeUpdate();

        insertQuery = "INSERT INTO SetHasTag (sid, tid) VALUES ('samplesetone', '2')";
        insertStatement = connection.prepareStatement(insertQuery);
        insertStatement.executeUpdate();

        insertQuery = "INSERT INTO UserCreatesSets (uid, sid) VALUES ('hi', 'samplesetone')";
        insertStatement = connection.prepareStatement(insertQuery);
        insertStatement.executeUpdate();

        insertQuery = "INSERT INTO FolderHasSets (fid, sid) VALUES ('samplefolderone', 'samplesetone')";
        insertStatement = connection.prepareStatement(insertQuery);
        insertStatement.executeUpdate();

        //flashcard one
        insertQuery = "INSERT INTO Flashcards (flashid, favorite, front) VALUES ('flashidone', 'N', '1 + 1')";
        insertStatement = connection.prepareStatement(insertQuery);
        insertStatement.executeUpdate();

        insertQuery = "INSERT INTO FrontHasBack (front, back) VALUES ('1 + 1', '2')";
        insertStatement = connection.prepareStatement(insertQuery);
        insertStatement.executeUpdate();

        insertQuery = "INSERT INTO SetHasFlashcards (sid, flashid) VALUES ('samplesetone', 'flashidone')";
        insertStatement = connection.prepareStatement(insertQuery);
        insertStatement.executeUpdate();

        //flashcard two
        insertQuery = "INSERT INTO Flashcards (flashid, favorite, front) VALUES ('flashidtwo', 'N', '10 x 10')";
        insertStatement = connection.prepareStatement(insertQuery);
        insertStatement.executeUpdate();

        insertQuery = "INSERT INTO FrontHasBack (front, back) VALUES ('10 x 10', '100')";
        insertStatement = connection.prepareStatement(insertQuery);
        insertStatement.executeUpdate();

        insertQuery = "INSERT INTO SetHasFlashcards (sid, flashid) VALUES ('samplesetone', 'flashidtwo')";
        insertStatement = connection.prepareStatement(insertQuery);
        insertStatement.executeUpdate();

        //set two
        insertQuery = "INSERT INTO Sets (sid, name, author, date, description) VALUES ('samplesettwo', 'basic science', 'hi', '2023-08-08', 'elementary science!')";
        insertStatement = connection.prepareStatement(insertQuery);
        insertStatement.executeUpdate();

        insertQuery = "INSERT INTO SetHasTag (sid, tid) VALUES ('samplesettwo', '3')";
        insertStatement = connection.prepareStatement(insertQuery);
        insertStatement.executeUpdate();

        insertQuery = "INSERT INTO UserCreatesSets (uid, sid) VALUES ('hi', 'samplesettwo')";
        insertStatement = connection.prepareStatement(insertQuery);
        insertStatement.executeUpdate();

        //flashcard three
        insertQuery = "INSERT INTO Flashcards (flashid, favorite, front) VALUES ('flashidthree', 'N', 'mitochondria')";
        insertStatement = connection.prepareStatement(insertQuery);
        insertStatement.executeUpdate();

        insertQuery = "INSERT INTO FrontHasBack (front, back) VALUES ('mitochondria', 'powerhouse of the cell')";
        insertStatement = connection.prepareStatement(insertQuery);
        insertStatement.executeUpdate();

        insertQuery = "INSERT INTO SetHasFlashcards (sid, flashid) VALUES ('samplesettwo', 'flashidthree')";
        insertStatement = connection.prepareStatement(insertQuery);
        insertStatement.executeUpdate();

        //bye
        insertQuery = "INSERT INTO Users (uid, password) VALUES ('bye', 'bye')";
        insertStatement = connection.prepareStatement(insertQuery);
        insertStatement.executeUpdate();

        //bye's set
        insertQuery = "INSERT INTO Sets (sid, name, author, date, description) VALUES ('samplesetthree', 'basic science', 'bye', '2023-08-08', 'science is cool!')";
        insertStatement = connection.prepareStatement(insertQuery);
        insertStatement.executeUpdate();

        insertQuery = "INSERT INTO SetHasTag (sid, tid) VALUES ('samplesetthree', '3')";
        insertStatement = connection.prepareStatement(insertQuery);
        insertStatement.executeUpdate();

        insertQuery = "INSERT INTO UserCreatesSets (uid, sid) VALUES ('bye', 'samplesetthree')";
        insertStatement = connection.prepareStatement(insertQuery);
        insertStatement.executeUpdate();

        //bye's 1st flashcards
        insertQuery = "INSERT INTO Flashcards (flashid, favorite, front) VALUES ('flashidfour', 'N', 'cells')";
        insertStatement = connection.prepareStatement(insertQuery);
        insertStatement.executeUpdate();

        insertQuery = "INSERT INTO FrontHasBack (front, back) VALUES ('cells', 'basic building blocks of life')";
        insertStatement = connection.prepareStatement(insertQuery);
        insertStatement.executeUpdate();

        insertQuery = "INSERT INTO SetHasFlashcards (sid, flashid) VALUES ('samplesetthree', 'flashidfour')";
        insertStatement = connection.prepareStatement(insertQuery);
        insertStatement.executeUpdate();

        //bye's 2nd flashcards
        insertQuery = "INSERT INTO Flashcards (flashid, favorite, front) VALUES ('flashidfive', 'N', 'deforestation')";
        insertStatement = connection.prepareStatement(insertQuery);
        insertStatement.executeUpdate();

        insertQuery = "INSERT INTO FrontHasBack (front, back) VALUES ('deforestation', 'causes wildlife to lose their ecosystem and habitiat')";
        insertStatement = connection.prepareStatement(insertQuery);
        insertStatement.executeUpdate();

        insertQuery = "INSERT INTO SetHasFlashcards (sid, flashid) VALUES ('samplesetthree', 'flashidfive')";
        insertStatement = connection.prepareStatement(insertQuery);
        insertStatement.executeUpdate();

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
