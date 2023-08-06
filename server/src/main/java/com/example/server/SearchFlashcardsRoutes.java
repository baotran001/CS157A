package com.example.server;
import java.sql.*;
import java.util.ArrayList;
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
public class SearchFlashcardsRoutes {
    @GetMapping("/searchflashcards")
    public String displaySetsPage(@CookieValue(name = "user_uid", required = false) Cookie cookie, Model model) throws SQLException{
        if(cookie != null){
            model.addAttribute("cookieName",cookie.getValue());
        }
        Connection connection = Utility.createSQLConnection();
        Statement statement = connection.createStatement();
       
        return "searchflashcards";
    }
    @PostMapping("/searchflashcards")
    public String searchSets(@RequestParam("searched") String searchKeywords, Model model,
                            @CookieValue(name = "user_uid", required = false) Cookie cookie) throws SQLException {
        if(cookie != null){
            model.addAttribute("cookieName",cookie.getValue());
        }
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

            // Process the search results and add them to the searchResults ArrayList
            while (resultSet.next()) {
                String sid = resultSet.getString("sid");
                String name = resultSet.getString("name");
                String author = resultSet.getString("author");
                Date date = resultSet.getDate("date");
                String description = resultSet.getString("description");

                Sets sets = new Sets();
                sets.setSetid(sid);
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
}