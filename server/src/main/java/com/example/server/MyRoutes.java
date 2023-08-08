package com.example.server;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
public class MyRoutes {
    // Display register form
    @GetMapping("/register")
    public String displayRegisterForm(Model model){
        // Create model attribute "user" for post request
        model.addAttribute("user", new User());
        return "register";
    }

    // Post Request to retrieve user data and send to database
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, RedirectAttributes redirectAttributes) throws SQLException{
        // Get User parameters
        String username = user.getUsername().trim();
        String password = user.getPassword();

        // Check if username or password is empty after trimming
        if (username.isEmpty() || password.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Username or password cannot be empty or contain only spaces.");
            return "redirect:/quizMeDB/register";
        }
        
        // Add user to database
         // Establish SQL Connection
         Connection connection = Utility.createSQLConnection();
         // Statement Execution
         try{
             Statement statement = connection.createStatement();
             String query1 = "SELECT * FROM USERS WHERE uid = '" + username + "';";
             ResultSet resultSet = statement.executeQuery(query1);
             // check if registered data already exists in database. 
             if(resultSet.next()){
                // display error 
                redirectAttributes.addFlashAttribute("errorMessage", "username already used");
                return "redirect:/quizMeDB/register";
             }
             // no duplicate, add user into database
             String query = "INSERT INTO USERS (uid, password) " + "VALUES ('" 
             + username + "', '" + password + "');";
             // use executeUpdate for insert statements
             statement.executeUpdate(query);
             connection.close();
         }catch (SQLException E){
             System.out.println("SQLException:" + E.getMessage());
             System.out.println("SQLState:" + E.getSQLState());
             System.out.println("VendorError:" + E.getErrorCode());
         }
         redirectAttributes.addFlashAttribute("registerSuccess", "Registration was Successful");
        return "redirect:/quizMeDB/login"; // Return the name of the HTML template for the success page
    }

    @GetMapping("/login")
    public String login(Model model){
        model.addAttribute("user", new User());
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@ModelAttribute("user") User user, RedirectAttributes redirectAttributes,  HttpServletResponse response) throws SQLException{
        String username = user.getUsername();
        String password = user.getPassword();
        Connection connection = Utility.createSQLConnection();
         // Statement Execution
         try{
             Statement statement = connection.createStatement();
             String query = "SELECT * FROM USERS WHERE uid = '" + username + "' AND password = '" +
             password + "';";
             ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()){
                Cookie cookie = new Cookie("user_uid", username);
                cookie.setMaxAge(3600); // Set the cookie's expiration time in seconds (1 hour in this example)
                response.addCookie(cookie);
                return "redirect:/quizMeDB/home";
            }
             connection.close();
         }catch (SQLException E){
             System.out.println("SQLException:" + E.getMessage());
             System.out.println("SQLState:" + E.getSQLState());
             System.out.println("VendorError:" + E.getErrorCode());
         }
        // if user does not exist then display error message
        redirectAttributes.addFlashAttribute("errorMessage", "Incorrect username or password.");
        return "redirect:/quizMeDB/login";
    }

    //helper method for home
    private List<Folder> getUserFoldersFromDatabase(String uid) throws SQLException {
        List<Folder> userFolders = new ArrayList<>();
    
        // Establish a SQL connection and execute a query to retrieve user's folders
        Connection connection = Utility.createSQLConnection();
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM Folder WHERE author = '" + uid + "';";
            ResultSet resultSet = statement.executeQuery(query);
    
            while (resultSet.next()) {
                // Create a new Folder object and populate its fields from the resultSet
                Folder folder = new Folder();
                folder.setFolderid(resultSet.getString("fid"));
                folder.setName(resultSet.getString("name"));
                folder.setAuthor(resultSet.getString("author"));
                folder.setDescription(resultSet.getString("description"));
                userFolders.add(folder);
            }
    
            connection.close();
        } catch (SQLException e) {
            // Handle any exceptions
            e.printStackTrace();
        }
    
        return userFolders;
    }
    
    //get user's sets without a folder, helper method for home
    private List<Sets> getUserSetsWithoutFolder(String uid) throws SQLException {
        List<Sets> userSetsWithoutFolder = new ArrayList<>();
    
        // Establish a SQL connection and execute a query to retrieve user's sets without a folder
        Connection connection = Utility.createSQLConnection();
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM Sets WHERE sid IN " +
               "(SELECT sid FROM UserCreatesSets WHERE uid = '" + uid + "') " +
               "AND sid NOT IN (SELECT sid FROM FolderHasSets);";

            ResultSet resultSet = statement.executeQuery(query);
    
            while (resultSet.next()) {
                // Create a new Set object and populate its fields from the resultSet
                Sets set = new Sets();
                set.setSetid(resultSet.getString("sid"));
                set.setName(resultSet.getString("name"));
                set.setAuthor(resultSet.getString("author"));
                set.setDescription(resultSet.getString("description"));
                userSetsWithoutFolder.add(set);
            }
    
            connection.close();
        } catch (SQLException e) {
            // Handle any exceptions
            e.printStackTrace();
        }
    
        return userSetsWithoutFolder;
    }

    @GetMapping("/home")
    public String showHome(@CookieValue(name = "user_uid", required = false) Cookie cookie, Model model){
        // Assign cookieName as name of user
        if (cookie == null) {
            // Handle the case when the cookie is not present
            return "redirect:/quizMeDB/login"; // Redirect to the login page or an appropriate page
        }
        
        model.addAttribute("cookieName",cookie.getValue());

        try {
            // Retrieve user's folders from the database
            List<Folder> userFolders = getUserFoldersFromDatabase(cookie.getValue()); 
    
            model.addAttribute("userFolders", userFolders);
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception
        }

        try {
            // Retrieve user's folders from the database
            List<Folder> userFolders = getUserFoldersFromDatabase(cookie.getValue()); 

            // Retrieve user's sets without a folder from the database
            List<Sets> userSetsWithoutFolder = getUserSetsWithoutFolder(cookie.getValue()); 

            model.addAttribute("userFolders", userFolders);
            model.addAttribute("userSetsWithoutFolder", userSetsWithoutFolder);
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception
        }
        
        return "home";
    }

    //showing folder
    @GetMapping("/folder")
    public String showFolder(@RequestParam(name = "fid") String fid,
                            @RequestParam(name = "name") String name,
                            @CookieValue(name = "user_uid", required = false) Cookie cookie, Model model) throws SQLException {
        if (cookie == null) {
            // Handle the case when the cookie is not present
            return "redirect:/quizMeDB/login"; // Redirect to the login page or an appropriate page
        }

        model.addAttribute("cookieName",cookie.getValue());
        
        // Add folder info to the model
        model.addAttribute("folderId", fid);
        model.addAttribute("folderName", name);

        List<Sets> folderSets = new ArrayList<>();

        try {
            // Retrieve sets within the specified folder from the database
            Connection connection = Utility.createSQLConnection();
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM FolderHasSets fhs " +
                           "JOIN Sets s ON fhs.sid = s.sid " +
                           "WHERE fhs.fid = '" + fid + "';";
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                // Create a new Sets object and populate its fields from the resultSet
                Sets set = new Sets();
                set.setSetid(resultSet.getString("s.sid"));
                set.setName(resultSet.getString("s.name"));
                set.setAuthor(resultSet.getString("s.author"));
                set.setDate(resultSet.getDate("s.date"));
                set.setDescription(resultSet.getString("s.description"));
                folderSets.add(set);
            }
    
            connection.close();

            model.addAttribute("folderSets", folderSets);
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception
        }
        

        return "folder"; 
    }

    @GetMapping("/following")
    public String showFollowing(@CookieValue(name = "user_uid", required = false) Cookie cookie, Model model) throws SQLException{
        // Assign cookieName as name of user
        if (cookie == null) {
            // Handle the case when the cookie is not present
            return "redirect:/quizMeDB/login"; // Redirect to the login page or an appropriate page
        }
        
        String uid = cookie.getValue();
        model.addAttribute("cookieName",uid);

        List<User> userList = new ArrayList<>();

        try {
            Connection connection = Utility.createSQLConnection();
            // Retrieve user's following from the database
            Statement statement = connection.createStatement();
            String query = "SELECT F.uid AS following_uid, U.uid AS follower_uid " +
                "FROM UserHasFollowingList F " +
                "JOIN Users U ON F.uid = U.uid " +
                "WHERE F.fid = '" + uid + "';";
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                // Create a new Sets object and populate its fields from the resultSet
                User user = new User();
                user.setUsername(resultSet.getString("follower_uid"));
                user.setFollowing(resultSet.getString("following_uid"));
                userList.add(user);
            }
    
            connection.close();

            model.addAttribute("userList", userList);

        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception
        }
        
        return "following";
    }

    @GetMapping("/logout")
    public String logOff(@CookieValue(name = "user_uid", required = false) Cookie cookie, Model model, HttpServletResponse response){
        // delete cookie to log out
        if(cookie != null){
            cookie.setMaxAge(0);
            cookie.setValue(null);
            response.addCookie(cookie);
        }
        return "redirect:/quizMeDB/login";
    }
}
