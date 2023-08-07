package com.example.server;
import java.sql.*;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
        //System.out.println(uid + " " + email + " " + password);

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
             //System.out.println(query);
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
                return "redirect:/quizMeDB/home?uid=" + username;
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



    @GetMapping("/home")
    public String showHome(@CookieValue(name = "user_uid", required = false) Cookie cookie, Model model){
        // Assign cookieName as name of user
        if(cookie != null){
            model.addAttribute("cookieName",cookie.getValue());
        }

        
        return "home";
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
