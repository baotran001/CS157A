package com.example.server;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
public class SetsRoutes {

    private String folderName;

    public SetsRoutes() {
        this.folderName = "";
    }

    public String getFolderName(){
        return this.folderName;
    }

    public void setFolderName(String folderName){
        this.folderName = folderName;
    }



    public boolean isValidFid(String fid) throws SQLException {
        Connection connection = Utility.createSQLConnection();
        Statement statement = connection.createStatement();
        String query = "SELECT COUNT(*) FROM Folder WHERE fid = '" + fid + "';";
        ResultSet rs = statement.executeQuery(query);
        int count = 0;
        if (rs.next()) {
            count = rs.getInt(1);
        }
        connection.close();
        return count > 0;
    }

    @GetMapping("/sets")
    public String displaySetsPage(@RequestParam(name = "fid", required = false) String fidValue,
                                @CookieValue(name = "user_uid", required = false) Cookie cookie, Model model) throws SQLException{
        if (cookie == null) {
            // Handle the case when the cookie is not present
            return "redirect:/quizMeDB/login";
        }
        if(cookie != null){
            model.addAttribute("cookieName",cookie.getValue());
        }

        model.addAttribute("fName", getFolderName());
        model.addAttribute("fidVal", fidValue);
    
        Connection tagConnection = Utility.createSQLConnection();
        Statement tagStatement = tagConnection.createStatement();
        String queryTags = "SELECT * FROM Tag;";
        ResultSet resTags = tagStatement.executeQuery(queryTags);
        
        List<Tag> tags = new ArrayList<>();
        while (resTags.next()) {
            Tag tag = new Tag();
            tag.setTid(resTags.getString("tid"));
            tag.setTag_name(resTags.getString("tag_name"));
            tags.add(tag);

        }
        
        tagConnection.close();

        model.addAttribute("tags", tags);
        

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
    public String createSets(@RequestParam(name = "fid", required = false) String fid,
                             @RequestParam(name = "tag") String tag,
                             @ModelAttribute("sets") Sets set, RedirectAttributes redirectAttributes,        
                             @CookieValue(name = "user_uid", required = false) Cookie cookie, Model model) throws SQLException{
        
        String uid = cookie.getValue();
        set.setAuthor(uid); // Set the user's uid as the author
        
        String fidValue = set.getFid();

        String sid = set.getSid();
        String name = set.getName().trim();
        String author = set.getAuthor();
        java.sql.Date date = set.getDate();
        String description = set.getDescription().trim();
        
        // Check if set name and description are not empty or just spaces
        if (name.isEmpty() || description.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Set name and description cannot be empty or contain only spaces.");
            
            if(fidValue != null){
                return "redirect:/quizMeDB/sets?fid=" + fidValue;
            }
            
            return "redirect:/quizMeDB/sets";
        }

        Connection connection = Utility.createSQLConnection();
        Statement statement = connection.createStatement();
        String query = "Select Count(*) FROM Sets WHERE name = '" + name +
        "'" + "AND description = '" + description + "' AND author = '" + author + "';";
        ResultSet rs = statement.executeQuery(query);

        int count = 0;
        if (rs.next()) {
            count = rs.getInt("Count(*)");
        }
        if (count == 0){

            //insert into sets
            String query1 = "INSERT INTO sets (sid, name, author, date, description) " + "VALUES ('" 
            + sid + "', '" + name + "', '" + author  + "', '" + date + "', '" + description + "');";
            statement.executeUpdate(query1);
          
            //insert into user creates sets
            String query2 = "INSERT INTO UserCreatesSets (uid, sid) " + "VALUES ('" 
            + uid + "', '" + sid + "');";
            statement.executeUpdate(query2);

            // Insert into the set has tag table using the sid and tid
            String query4 = "INSERT INTO SetHasTag (sid, tid) " + "VALUES ('" 
            + sid + "', '" + tag + "');";
            statement.executeUpdate(query4);

          
            // Check if the fid parameter is not null, meaning a folder ID is provided in the URL
            if (fidValue != null) {
                
                //not valid id like blank id means there is no folder
                if (!isValidFid(fid)) {
                    redirectAttributes.addFlashAttribute("error", "Invalid Folder ID (fid)");
                    return "redirect:/quizMeDB/flashcard?sid=" + sid + "&name=" + name;
                }

                // Insert into the FolderHasSets table using the fid and sid
                String query3 = "INSERT INTO FolderHasSets (fid, sid) " + "VALUES ('" 
                + fidValue + "', '" + sid + "');";
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
