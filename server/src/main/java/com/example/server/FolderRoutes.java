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
public class FolderRoutes {
    @GetMapping("/folder")
    public String displayfolderPage(@CookieValue(name = "user_uid", required = false) Cookie cookie, Model model) throws SQLException{
        if(cookie != null){
            model.addAttribute("cookieName",cookie.getValue());
        }
        model.addAttribute("folder", new Folder());
        Connection connection = Utility.createSQLConnection();
        Statement statement = connection.createStatement();
        String query = "Select * FROM folder;";
        ResultSet res = statement.executeQuery(query);
        ArrayList<Folder> folderArr = new ArrayList<>();
        while (res.next()) {
            String fid = res.getString("fid");
            String name = res.getString("name");
            String author = res.getString("author");
            String description = res.getString("description");
            Folder folder = new Folder();
            folder.setFolderid(fid);
            folder.setName(name);
            folder.setAuthor(author);
            folder.setDescription(description);
            folderArr.add(folder);
            // ... Print other columns as needed
        }
        model.addAttribute("dataList", folderArr);
        return "createfolder";
    }
    @PostMapping("/createFolder")
    public String createFolder(@ModelAttribute("folder") Folder folder, RedirectAttributes redirectAttributes,
                             @CookieValue(name = "user_uid", required = false) Cookie cookie) throws SQLException{
        
        String uid = cookie.getValue();
        folder.setAuthor(uid); // Set the user's uid as the author
        
        String fid = folder.getFid();
        String name = folder.getName();
        String author = folder.getAuthor();
        String description = folder.getDescription();
        //System.out.println(sid + " " + author + " " + name + " " + description + " " + date);
        
        Connection connection = Utility.createSQLConnection();
        Statement statement = connection.createStatement();
        String query1 = "INSERT INTO folder (fid, name, author, description) " + "VALUES ('" 
        + fid + "', '" + name + "', '" + author  + "', '" + description + "');";
        statement.executeUpdate(query1);

        String query2 = "INSERT INTO UsersCreatesFolder (uid, fid) " + "VALUES ('" 
        + uid + "', '" + fid + "');";
        statement.executeUpdate(query2);

        connection.close();
        redirectAttributes.addFlashAttribute("success", "Successful Folder Creation!");
        return "redirect:/quizMeDB/sets?fid=" + fid;
    }
}
