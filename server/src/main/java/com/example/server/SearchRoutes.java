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

    private ArrayList<Sets> setList;
    private String filterTag;

    public SearchRoutes(){
        this.setList = new ArrayList<>();
        this.filterTag = "noValue";
    }

    public void setFilterTag(String x){
        this.filterTag = x;
    }

    public String getFilterTag(){
        return this.filterTag;
    }

    public void setSetList(ArrayList<Sets> s){
        this.setList = new ArrayList<>(s);
    } 

    public ArrayList<Sets> getSetList(){
        return this.setList;
    }

    @GetMapping("/searchflashcards")
    public String displaySetsPage(   
    @CookieValue(name = "user_uid", required = false) Cookie cookie, Model model) throws SQLException{
        if(cookie != null){
            model.addAttribute("cookieName",cookie.getValue());
        }
        ArrayList<Sets> filteredSets = new ArrayList<>();
        if(!getFilterTag().equals("noValue")){
            for(int i = 0; i < getSetList().size(); i++){
                if(getSetList().get(i).getTag().equals(getFilterTag())){
                    filteredSets.add(getSetList().get(i));
                }
            }
            model.addAttribute("flashcardSets", filteredSets);
        } else {
            model.addAttribute("flashcardSets", getSetList());
        }
        return "searchflashcards";
    }

    @PostMapping("/filterTag")
    public String setFilter(   
    @RequestParam("setName") String setName,
    @RequestParam("setTag") String filterTag,
    @CookieValue(name = "user_uid", required = false) Cookie cookie, Model model) throws SQLException{
        setFilterTag(filterTag);
        return searchSets(setName, model, cookie);
    }

    @PostMapping("/searchflashcards")
    public String searchSets( 
        @RequestParam("searched") String searchKeywords , Model model,
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

                    String sid = resultSet.getString("sid");
                    String name = resultSet.getString("name");
                    String author = resultSet.getString("author");
                    Date date = resultSet.getDate("date");
                    String description = resultSet.getString("description");

                 //DELETE THIS so it works
                    String tagquery = "SELECT t.tid, t.tag_name FROM Tag t " +
                    "INNER JOIN SetHasTag st ON t.tid = st.tid " +
                    "WHERE st.sid = ?";
                    
                    PreparedStatement tagstatement = connection.prepareStatement(tagquery);
                    tagstatement.setString(1, sid);

                    ResultSet tagResultSet = tagstatement.executeQuery();

                    Sets sets = new Sets();

                    if (tagResultSet.next()) {
                        String tag = tagResultSet.getString("tag_name");
                        sets.setSetid(sid);
                        sets.setName(name);
                        sets.setAuthor(author);
                        sets.setDate(date);
                        sets.setDescription(description);
                        sets.setTag(tag);
                    } else {
                    }
                    

                    if (cookie != null) {
                        boolean hasSet = true;
                        PreparedStatement checkStatement = null;

                        String loggedInUserUid = cookie.getValue();
                        String checkQuery = "SELECT COUNT(*) FROM UserCreatesSets WHERE uid = ? AND sid = ?";
                        checkStatement = connection.prepareStatement(checkQuery);
                        checkStatement.setString(1, loggedInUserUid);
                        checkStatement.setString(2, sid);
                        ResultSet result = checkStatement.executeQuery();
            
                        if (result.next() && result.getInt("COUNT(*)") == 0) {
                            // The current user does not have the set
                            hasSet = false;
                        }
                        sets.setHasSet(hasSet);
                        model.addAttribute("hasSet", hasSet);
                    }
                searchResults.add(sets);
            }
        model.addAttribute("flashcardSets", searchResults);
        setSetList(searchResults);
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

        ArrayList<Sets> filteredSets = new ArrayList<>();
        if(!getFilterTag().equals("noValue")){
            for(int i = 0; i < getSetList().size(); i++){
                if(getSetList().get(i).getTag().equals(getFilterTag())){
                    filteredSets.add(getSetList().get(i));
                }
            }
            model.addAttribute("flashcardSets", filteredSets);
        } else {
            model.addAttribute("flashcardSets", getSetList());
        }
        model.addAttribute("filterTag", getFilterTag());
        return "searchflashcards"; // Return the same view to display the search results.
    }

    @PostMapping("/addSet")
    public String addSet(@RequestParam("searched") String sidValue, @RequestParam("setName") String setName, @RequestParam("setAuthor") String setAuthor,
    Model model,  @CookieValue(name = "user_uid", required = false) Cookie cookie) throws SQLException {
        if(cookie != null){
            Connection connection = Utility.createSQLConnection();
            Statement statement = connection.createStatement();
            String query = "INSERT INTO UserCreatesSets (uid, sid) " + 
            "VALUES ('" + cookie.getValue() + "', '" + sidValue + "');";
            statement.executeUpdate(query);
        }
        return searchSets(setName, model, cookie);
        //"redirect:/quizMeDB/searchflashcards"
    }

    @PostMapping("/removeSet")
    public String deleteSet(@RequestParam("searched") String sidValue, @RequestParam("setName") String setName, @RequestParam("setAuthor") String setAuthor,
    Model model,  @CookieValue(name = "user_uid", required = false) Cookie cookie) throws SQLException {
        if(cookie != null){
            Connection connection = Utility.createSQLConnection();
            Statement statement = connection.createStatement();
            String query = "DELETE FROM UserCreatesSets WHERE uid = '" + 
            cookie.getValue() + "' AND sid = '" + sidValue + "';";
            statement.executeUpdate(query);
        }
        
        return searchSets(setName, model, cookie);
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