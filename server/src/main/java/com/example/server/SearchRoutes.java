package com.example.server;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.naming.spi.DirStateFactory.Result;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/quizMeDB")
public class SearchRoutes {
    @GetMapping("/searchflashcards")
    public String displaySetsPage(@CookieValue(name = "user_uid", required = false) Cookie cookie, Model model) throws SQLException{
        if(cookie != null){
            model.addAttribute("cookieName",cookie.getValue());
        }
       
        return "searchflashcards";
    }
    @PostMapping("/searchflashcards")
    public String searchSets(@RequestParam("searched") String searchKeywords, Model model,
                            @CookieValue(name = "user_uid", required = false) Cookie cookie) throws SQLException {
        if(cookie != null){
            model.addAttribute("cookieName",cookie.getValue());
        }
        // Check if searchKeywords is null or empty, and provide a default value if necessary
        String searchQuery = (searchKeywords != null && !searchKeywords.isEmpty()) ? searchKeywords : "Not specified";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        ArrayList<Sets> searchResults = new ArrayList<>();
         try {
            // Establish a connection to the database
            connection = Utility.createSQLConnection();

            // Prepare the SQL query to search for flashcard sets
            String query = "SELECT * FROM Sets WHERE name LIKE ? OR description LIKE ?";
            statement = connection.prepareStatement(query);

            // Set the searchKeywords as the parameter in the query
            String searchString = "%" + searchKeywords + "%";
            statement.setString(1, searchString);
            statement.setString(2, searchString);

            // Execute the query and get the result set
            resultSet = statement.executeQuery();
             // Add the search query to the model as an attribute.
            model.addAttribute("searched", searchQuery);
            // Process the search results and add them to the searchResults ArrayList
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String author = resultSet.getString("author");
                Date date = resultSet.getDate("date");
                String description = resultSet.getString("description");

                Sets sets = new Sets();
                sets.setName(name);
                sets.setAuthor(author);
                sets.setDate(date);
                sets.setDescription(description);

                searchResults.add(sets);
            }

            // Add the flashcard set search results to the model as an attribute.
            model.addAttribute("flashcardSets", searchResults);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close the result set, statement, and connection
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }

        return "searchflashcards"; // Return the same view to display the search results.
    }
    @PostMapping("/searchusers")
    public String searchUser(@RequestParam("searched") String searchKeywords, Model model,
                             @CookieValue(name = "user_uid", required = false) Cookie cookie) throws SQLException {
        if (cookie != null) {
            model.addAttribute("cookieName", cookie.getValue());
        }
    
        // Check if searchKeywords is null or empty, and provide a default value if necessary
        String searchQuery = (searchKeywords != null && !searchKeywords.isEmpty()) ? searchKeywords : "Not specified";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Map<String, Integer> searchResults = new HashMap<>(); // Using a HashMap to store name and following count.
    
        try {
            // Establish a connection to the database
            connection = Utility.createSQLConnection();
    
            // Prepare the SQL query to search for users by name and get the following count
            String query = "SELECT u.uid, COUNT(f.fid) AS followingCount " +
                           "FROM Users u " +
                           "LEFT JOIN UserHasFollowingList f ON u.uid = f.fid AND f.fid != u.uid " +
                           "WHERE u.uid = ? " +
                           "GROUP BY u.uid";
            statement = connection.prepareStatement(query);
    
            // Set the searchKeywords as the parameter in the query
            statement.setString(1, searchKeywords);
    
            // Execute the query and get the result set
            resultSet = statement.executeQuery();
    
            // Process the search results and add user names and following count to the searchResults HashMap
            while (resultSet.next()) {
                String name = resultSet.getString("uid");
                int followingCount = resultSet.getInt("followingCount");
                searchResults.put(name, followingCount);
            }
    
            // Add the user search results to the model as an attribute.
            model.addAttribute("users", searchResults);
            // Add the search query to the model as an attribute.
            model.addAttribute("searched", searchQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close the result set, statement, and connection
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    
        return "searchusers"; // Return the same view to display the search results.
    }
    
}