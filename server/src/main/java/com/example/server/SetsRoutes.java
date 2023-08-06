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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/quizMeDB")
public class SetsRoutes {
    @GetMapping("/sets")
    public String displaySetsPage(@CookieValue(name = "user_uid", required = false) Cookie cookie, Model model) throws SQLException{
        if(cookie != null){
            model.addAttribute("cookieName",cookie.getValue());
        }
        model.addAttribute("sets", new Sets());
        Connection connection = Utility.createSQLConnection();
        Statement statement = connection.createStatement();
        String query = "Select * FROM sets;";
        ResultSet res = statement.executeQuery(query);
        ArrayList<Sets> setsArr = new ArrayList<>();
        while (res.next()) {
            String sid = res.getString("sid");
            String name = res.getString("name");
            String author = res.getString("author");
            java.sql.Date date = res.getDate("date");
            String description = res.getString("description");
            Sets sets = new Sets();
            sets.setSetid(sid);
            sets.setName(name);
            sets.setAuthor(author);
            sets.setDate(date);
            sets.setDescription(description);
            setsArr.add(sets);
            // ... Print other columns as needed
        }
        model.addAttribute("dataList", setsArr);
        return "createset";
    }
    @PostMapping("/createSets")
    public String createSets(@ModelAttribute("sets") Sets set, RedirectAttributes redirectAttributes,
                             @CookieValue(name = "user_uid", required = false) Cookie cookie) throws SQLException{
        
        if (cookie != null) {
            String uid = cookie.getValue();
            set.setAuthor(uid); // Set the user's uid as the author
        }
        String sid = set.getSid();
        String name = set.getName();
        String author = set.getAuthor();
        java.sql.Date date = set.getDate();
        String description = set.getDescription();
        //System.out.println(sid + " " + author + " " + name + " " + description + " " + date);
        
        Connection connection = Utility.createSQLConnection();
        Statement statement = connection.createStatement();
        String query = "INSERT INTO sets (sid, name, author, date, description) " + "VALUES ('" 
        + sid + "', '" + name + "', '" + author  + "', '" + date + "', '" + description + "');";
        statement.executeUpdate(query);
        connection.close();
        redirectAttributes.addFlashAttribute("success", "Success!");
        return "redirect:/quizMeDB/sets";
    }
}
