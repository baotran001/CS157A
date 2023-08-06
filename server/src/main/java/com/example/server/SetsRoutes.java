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
public class SetsRoutes {
    @GetMapping("/sets")
    public String displaySetsPage(@RequestParam(name = "fid", required = false) String fid,
                                @CookieValue(name = "user_uid", required = false) Cookie cookie, Model model) throws SQLException{
        if(cookie != null){
            model.addAttribute("cookieName",cookie.getValue());
        }
        model.addAttribute("sets", new Sets());
        model.addAttribute("fid", fid);
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
                             @RequestParam(name = "fid", required = false) String fid,         
                             @CookieValue(name = "user_uid", required = false) Cookie cookie, Model model) throws SQLException{
        
        String uid = cookie.getValue();
        set.setAuthor(uid); // Set the user's uid as the author
        
        String sid = set.getSid();
        String name = set.getName();
        String author = set.getAuthor();
        java.sql.Date date = set.getDate();
        String description = set.getDescription();
        //System.out.println(sid + " " + author + " " + name + " " + description + " " + date);
        
        Connection connection = Utility.createSQLConnection();
        Statement statement = connection.createStatement();
        String query = "Select Count(*) FROM Sets WHERE name = '" + name +
        "'" + "AND description = '" + description + "' AND author = '" + author + "';";
        ResultSet rs = statement.executeQuery(query);

        int count = 0;
        if (rs.next()) {
            count = rs.getInt("Count(*)");
        }
        System.out.println(count);
        if (count == 0){
            System.out.println(sid);
            String query1 = "INSERT INTO sets (sid, name, author, date, description) " + "VALUES ('" 
            + sid + "', '" + name + "', '" + author  + "', '" + date + "', '" + description + "');";
            statement.executeUpdate(query1);
          
            String query2 = "INSERT INTO UserCreatesSets (uid, sid) " + "VALUES ('" 
            + uid + "', '" + sid + "');";
            statement.executeUpdate(query2);
          
            // Check if the fid parameter is not null, meaning a folder ID is provided in the URL
            if (fid != null) {
            // Insert into the FolderHasSets table using the fid and sid
            String query3 = "INSERT INTO FolderHasSets (fid, sid) " + "VALUES ('" 
                    + fid + "', '" + sid + "');";
            statement.executeUpdate(query3);
        }
            redirectAttributes.addFlashAttribute("success", "Successful Set Creation!");
            return "redirect:/quizMeDB/flashcard?sid=" + sid + "&name=" + name;
        }
        

        connection.close();
        redirectAttributes.addFlashAttribute("error", "Set Already Exists!");
        return "redirect:/quizMeDB/sets";
    }
}
