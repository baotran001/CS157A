package com.example.server;
import java.sql.*;
import java.util.ArrayList;
import javax.naming.spi.DirStateFactory.Result;

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
public class FlashcardRoutes {
    @GetMapping("/flashcard")
    public String displayFlashcardsPage(@CookieValue(name = "user_uid", required = false) Cookie cookie, Model model) throws SQLException{
        if(cookie != null){
            model.addAttribute("cookieName",cookie.getValue());
        }
        model.addAttribute("flashcard", new FlashCard());
        Connection connection = Utility.createSQLConnection();
        Statement statement = connection.createStatement();
        String query = "Select * FROM flashcards;";
        ResultSet res = statement.executeQuery(query);
        ArrayList<FlashCard> flashcArr = new ArrayList<>();
        while (res.next()) {
            String flashid = res.getString("flashid");
            String favorite = res.getString("favorite");
            String front = res.getString("front");
            String back = res.getString("back");
            FlashCard flashc = new FlashCard();
            flashc.setFlashid(flashid);
            flashc.setFavorite(favorite);
            flashc.setFront(front);
            flashc.setBack(back);
            flashcArr.add(flashc);
            // ... Print other columns as needed
        }
        model.addAttribute("dataList", flashcArr);
        return "flashcard";
    }
    @PostMapping("/createFlashcard")
    public String createFlashcard(@ModelAttribute("flashcard") FlashCard flashcard, RedirectAttributes redirectAttributes) throws SQLException{
        String flashid = flashcard.getFlashid();
        String favorite = flashcard.getFavorite();
        String front = flashcard.getFront();
        String back = flashcard.getBack();

        Connection connection = Utility.createSQLConnection();
        Statement statement = connection.createStatement();
        String query = "INSERT INTO flashcards (flashid, favorite, front, back) " + "VALUES ('" 
        + flashid + "', '" + favorite + "', '" + front + "', '" + back + "');";
        statement.executeUpdate(query);
        connection.close();
        redirectAttributes.addFlashAttribute("success", "Success!");
        return "redirect:/quizMeDB/flashcard";
    }
}
