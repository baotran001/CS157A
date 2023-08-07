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
    public String searchSets(@RequestParam("searched") String searchKeywords , Model model, @RequestParam("setTag") String setTag,
                            @CookieValue(name = "user_uid", required = false) Cookie cookie) throws SQLException {
       
        System.out.println("Searched: " + searchKeywords);
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
            String query = "SELECT * FROM Sets WHERE name LIKE ?";
            statement = connection.prepareStatement(query);

            // Set the searchKeywords as the parameter in the query
            String searchString = "%" + searchKeywords + "%";
            statement.setString(1, searchString);

            // Execute the query and get the result set
            resultSet = statement.executeQuery();
             // Add the search query to the model as an attribute.
            model.addAttribute("searched", searchQuery);
            // Process the search results and add them to the searchResults ArrayList
            while (resultSet.next()) {

                //IF THERE IS NOT SET TAG
                if(setTag.equals("noValue")){
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
                    model.addAttribute("flashcardSets", searchResults);

                    if (cookie != null) {
                    boolean hasSet = true;
                        PreparedStatement checkStatement = null;
                        String loggedInUserUid = cookie.getValue();
                        String checkQuery = "SELECT COUNT(*) FROM UserCreatesSets WHERE uid = ? AND sid = ?";
                        checkStatement = connection.prepareStatement(checkQuery);
                        //System.out.println("USER IDT?: " + loggedInUserUid);
                        //System.out.println("SET ID?: " + sid);
                        checkStatement.setString(1, loggedInUserUid);
                        checkStatement.setString(2, sid);
                        ResultSet result = checkStatement.executeQuery();
            
                        if (result.next() && result.getInt("COUNT(*)") == 0) {
                            // The current user does not have the set
                            hasSet = false;
                        }

                        System.out.println("GOT SET?: " + hasSet);
                        model.addAttribute("hasSet", hasSet);
                    }
                }else{
                    if (setTag != null && !setTag.isEmpty()) {
                        //System.out.println("Set Tag is:" + setTag);
                        String sid = resultSet.getString("sid");
                        PreparedStatement tagStatement = null;
                        String tagQuery = "SELECT tid FROM Tag WHERE tag_name = ?";
                        tagStatement = connection.prepareStatement(tagQuery);
                        tagStatement.setString(1, setTag);
                        ResultSet tagResultSet = tagStatement.executeQuery();
        
                        if (tagResultSet.next()) {
                            //System.out.println("THERE ARE TAGS" + setTag);
                            String tid = tagResultSet.getString("tid");
                            String setTagQuery = "SELECT sid FROM SetHasTag WHERE tid = ? AND sid = ?";
                            PreparedStatement setTagStatement = connection.prepareStatement(setTagQuery);
                            setTagStatement.setString(1, tid);
                            setTagStatement.setString(2, sid);
                            ResultSet setTagResultSet = setTagStatement.executeQuery();
        
                            if (setTagResultSet.next()) {
                                Sets taggedSet = new Sets();
                                taggedSet.setSetid(sid);
                                System.out.println("THIS IS SID: " + sid);
                                // Fetch other details of the set if needed

                                String name = resultSet.getString("name");
                                System.out.println("THIS IS NAME: " + name);
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
                                model.addAttribute("flashcardSets", searchResults);

                                 if (cookie != null) {
                                    boolean hasSet = true;
                                        PreparedStatement checkStatement = null;
                                        String loggedInUserUid = cookie.getValue();
                                        String checkQuery = "SELECT COUNT(*) FROM UserCreatesSets WHERE uid = ? AND sid = ?";
                                        checkStatement = connection.prepareStatement(checkQuery);
                                        //System.out.println("USER IDT?: " + loggedInUserUid);
                                        //System.out.println("SET ID?: " + sid);
                                        checkStatement.setString(1, loggedInUserUid);
                                        checkStatement.setString(2, sid);
                                        ResultSet result = checkStatement.executeQuery();
                            
                                        if (result.next() && result.getInt("COUNT(*)") == 0) {
                                            // The current user does not have the set
                                            hasSet = false;
                                        }

                                        System.out.println("GOT SET?: " + hasSet);
                                        model.addAttribute("hasSet", hasSet);
                                 }
                            }
                        }
                    }
                
                }
                
            }

            
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

    @PostMapping("/addSet")
    public String addSet(@RequestParam("searched") String searchKeywords, @RequestParam("setName") String setName, @RequestParam("setAuthor") String setAuthor,
    Model model,  @CookieValue(name = "user_uid", required = false) Cookie cookie) throws SQLException {
        String setTag = "noValue";
        if (cookie != null) {
            String loggedInUserUid = cookie.getValue();
            Connection connection = null;
            PreparedStatement checkStatement = null;
            PreparedStatement insertStatement = null;
        
            try {
                // Establish a connection to the database
                connection = Utility.createSQLConnection();
                
               // Check if the current user has the set
               boolean hasSet = true;
               String checkQuery = "SELECT COUNT(*) FROM UserCreatesSets WHERE uid = ? AND sid = ?";
               checkStatement = connection.prepareStatement(checkQuery);
               checkStatement.setString(1, loggedInUserUid);
               checkStatement.setString(2, searchKeywords);
               ResultSet resultSet = checkStatement.executeQuery();
   
               if (resultSet.next() && resultSet.getInt("COUNT(*)") == 0) {
                   // The current user does not have the set
                   hasSet = false;
               }
            System.out.println("checking before I add <3" + hasSet);
               if (hasSet) {
                if(loggedInUserUid.equals(setAuthor)){
                String deleteQuery = "DELETE FROM Sets WHERE  sid = ?";
                   insertStatement = connection.prepareStatement(deleteQuery);
                   insertStatement.setString(1, searchKeywords);
                   insertStatement.executeUpdate();
                   hasSet = false;// Update hasSet to false since we removed the set.
                }else{
                     // The current user has the set, so we need to remove it.
                   String deleteQuery = "DELETE FROM UserCreatesSets WHERE uid = ? AND sid = ?";
                   insertStatement = connection.prepareStatement(deleteQuery);
                   insertStatement.setString(1, loggedInUserUid);
                   insertStatement.setString(2, searchKeywords);
                   insertStatement.executeUpdate();
                   hasSet = false;// Update hasSet to false since we removed the set.
                }
                  

                     System.out.println("INSIDE SeT remove");
                } else {
                    // The current user does not have the set, so we need to add it.
                    String insertQuery = "INSERT INTO UserCreatesSets (uid, sid) VALUES (?, ?)";
                    insertStatement = connection.prepareStatement(insertQuery);
                    insertStatement.setString(1, loggedInUserUid);
                    insertStatement.setString(2, searchKeywords);
                    insertStatement.executeUpdate();
                    hasSet = true;// Update hasSet to true since we added the set.

                    System.out.println("INSIDE SeT INSERT");
                }
                
                //FINDing the tag name 
                // Update hasSet in the model

                String tagQuery = "SELECT t.tag_name FROM Tag t " +
                  "JOIN SetHasTag sht ON t.tid = sht.tid " +
                  "WHERE sht.sid = ?";
                PreparedStatement tagStatement = connection.prepareStatement(tagQuery);
                tagStatement.setString(1, searchKeywords);
                ResultSet tagResultSet = tagStatement.executeQuery();
                if (tagResultSet.next()) {
                    setTag = tagResultSet.getString("tag_name");
}
                model.addAttribute("hasSet", hasSet);

                System.out.println("Do they have set:" + hasSet);
    
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                // Close the statements and connection
                if (checkStatement != null) {
                    checkStatement.close();
                }
                if (insertStatement != null) {
                    insertStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            }
        }

        
        // Run the searchSets method to display the search results with the updated hasSet status
        return searchSets(setName, model, setTag, cookie);
        //"redirect:/quizMeDB/searchflashcards"
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
    
            // Prepare the SQL query to search for users by name
            String query = "SELECT uid FROM Users WHERE uid = ?";
            statement = connection.prepareStatement(query);
    
            // Set the searchKeywords as the parameter in the query
            statement.setString(1, searchKeywords);
    
            // Execute the query and get the result set
            resultSet = statement.executeQuery();
    
            // Process the search results and add user names and following count to the searchResults HashMap
            while (resultSet.next()) {
                String uid = resultSet.getString("uid");
    
                // Now, execute the following count query for this user
                String followingCountQuery = "SELECT COUNT(fid) AS followingCount FROM UserHasFollowingList WHERE uid = ?";
                PreparedStatement followingCountStatement = connection.prepareStatement(followingCountQuery);
                followingCountStatement.setString(1, uid);
                ResultSet followingCountResultSet = followingCountStatement.executeQuery();
    
                // Process the following count and add the user and following count to the searchResults HashMap
                if (followingCountResultSet.next()) {
                    int followingCount = followingCountResultSet.getInt("followingCount");
                    searchResults.put(uid, followingCount);
                }
    
                // Close the following count result set and statement
                followingCountResultSet.close();
                followingCountStatement.close();
            }
    
            // Check if the current user is following the searched user
            boolean isFollowing = false;
        if (cookie != null) {
            String loggedInUserUid = cookie.getValue();
            String checkFollowingQuery = "SELECT COUNT(*) FROM UserHasFollowingList WHERE uid = ? AND fid = ?";
            PreparedStatement checkFollowingStatement = connection.prepareStatement(checkFollowingQuery);
            checkFollowingStatement.setString(1, searchKeywords);
            checkFollowingStatement.setString(2, loggedInUserUid);
            ResultSet checkFollowingResultSet = checkFollowingStatement.executeQuery();
            if (checkFollowingResultSet.next() && checkFollowingResultSet.getInt(1) > 0) {
                // The current user is following the searched user
                isFollowing = true;
            }
            checkFollowingResultSet.close();
            checkFollowingStatement.close();
        }
    
            // Add the user search results and the isFollowing flag to the model
            model.addAttribute("users", searchResults);
            model.addAttribute("searched", searchQuery);
            System.out.println("isfollowing in search:" + isFollowing);
            model.addAttribute("isFollowing", isFollowing);
            boolean noUsersFound = searchResults.isEmpty();
            model.addAttribute("noUsersFound", noUsersFound);
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
    @PostMapping("/follow")
    public String follow(@RequestParam("searched") String searchKeywords, Model model,
                         @CookieValue(name = "user_uid", required = false) Cookie cookie) throws SQLException {
        if (cookie != null) {
            String loggedInUserUid = cookie.getValue(); // Get the UID of the logged-in user from the cookie.
            //System.out.println("Search user method called with searchKeywords: " + searchKeywords);
            //System.out.println("loggedInUserUid: " + loggedInUserUid);
            if (searchKeywords != null && !searchKeywords.isEmpty() && !searchKeywords.equals(loggedInUserUid)) {
                Connection connection = null;
                PreparedStatement checkStatement = null;
                PreparedStatement insertStatement = null;
                PreparedStatement unfollowStatement = null;
                PreparedStatement followStatement = null;
    
                try {
                    // Establish a connection to the database
                    connection = Utility.createSQLConnection();
    
                    // Check if the current user is following the searched user
                    boolean isFollowing = true;
                    // Prepare the SQL query to check if the follower-following relationship already exists
                    String checkQuery = "SELECT COUNT(*) FROM UserHasFollowingList WHERE uid = ? AND fid = ?";
                    checkStatement = connection.prepareStatement(checkQuery);

                    // Set the logged-in user's UID as the parameter for the first ? in the check query
                    checkStatement.setString(1, searchKeywords);

                    // Set the user to be followed (searched user's UID) as the parameter for the second ? in the check query
                    checkStatement.setString(2, loggedInUserUid);

                    // Execute the check query to see if the relationship already exists
                    ResultSet resultSet = checkStatement.executeQuery();;
                    if (resultSet.next() && resultSet.getInt(1) == 0) {
                        // The current user is following the searched user
                        isFollowing = false;
                    }
    
                    if (isFollowing) {
                        //System.out.println("UnFollow method called!");
                        // The current user is following the searched user, so we need to unfollow them.
                        String unfollowQuery = "DELETE FROM UserHasFollowingList WHERE uid = ? AND fid = ?";
                        unfollowStatement = connection.prepareStatement(unfollowQuery);

                        // Set the logged-in user's UID as the parameter for the first ? in the insert query
                        unfollowStatement.setString(1, searchKeywords);

                        // Set the user to be followed (searched user's UID) as the parameter for the second ? in the insert query
                        unfollowStatement.setString(2, loggedInUserUid);

                        // Execute the insert query to add the follower-following relationship
                        unfollowStatement.executeUpdate();
    
                        // Update isFollowing to false since we just unfollowed the user
                        isFollowing = false;
                    } else {
                        //System.out.println("Follow method called!");
                        // The current user is not following the searched user, so we need to follow them.
                        String followQuery = "INSERT INTO UserHasFollowingList (uid, fid) VALUES (?, ?)";
                        followStatement = connection.prepareStatement(followQuery);

                        // Set the logged-in user's UID as the parameter for the first ? in the insert query
                        followStatement.setString(1, searchKeywords);

                        // Set the user to be followed (searched user's UID) as the parameter for the second ? in the insert query
                        followStatement.setString(2, loggedInUserUid);

                        // Execute the insert query to add the follower-following relationship
                        followStatement.executeUpdate();
    
                        // Update isFollowing to true since we just followed the user
                        isFollowing = true;
                    }
    
                    // Update isFollowing in the model
                    model.addAttribute("isFollowing", isFollowing);
                    //System.out.println("Is following: " + isFollowing);
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    // Close the statements and connection
                    if (checkStatement != null) {
                        checkStatement.close();
                    }
                    if (insertStatement != null) {
                        insertStatement.close();
                    }
                    if (connection != null) {
                        connection.close();
                    }
                }
            }
        }
    
        // Add the searchKeywords to the model to be used in the searchUser method
        model.addAttribute("searched", searchKeywords);
    
        // Run the searchUser method to display the search results with the updated following count
        return searchUser(searchKeywords, model, cookie);
    }
             
}