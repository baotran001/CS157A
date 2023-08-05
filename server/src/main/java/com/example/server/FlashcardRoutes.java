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
    public String displayFlashcardsPage(RedirectAttributes redirectAttributes, @CookieValue(name = "user_uid", required = false) Cookie cookie, Model model) throws SQLException{
        if(cookie != null){
            model.addAttribute("cookieName",cookie.getValue());
        }
        model.addAttribute("flashcard", new FlashCard());
        Connection connection = Utility.createSQLConnection();
        Statement statement = connection.createStatement();
        String query = "Select F.flashid, F.favorite, F.front, FB.back FROM flashcards F, FrontHasBack FB" +
        " WHERE F.front = FB.front;";
        ResultSet res = statement.executeQuery(query);
        ArrayList<FlashCard> flashcArr = new ArrayList<>();
        while (res.next()) {
            String flashid = res.getString("flashid");
            String favorite = res.getString("favorite");
            String front = res.getString("front");
            String back = res.getString("back");

            FlashCard flashc = new FlashCard();
            System.out.println(front + " " + back);
            flashc.setFlashid(flashid);
            flashc.setFavorite(favorite);
            flashc.setFront(front);
            flashc.setBack(back);
            flashcArr.add(flashc);
        }
        model.addAttribute("dataList", flashcArr);
        return "flashcard";
    }
    @PostMapping("/createFlashcard")
    public String createFlashcard(Model model, @ModelAttribute("flashcard") FlashCard flashcard, RedirectAttributes redirectAttributes) throws SQLException{
        String flashid = flashcard.getFlashid();
        String favorite = flashcard.getFavorite();
        String front = flashcard.getFront();
        String back = flashcard.getBack();

        Connection connection = Utility.createSQLConnection();
        Statement statement = connection.createStatement();
        try{
            String query1 = "INSERT INTO FrontHasBack (front, back) " + "VALUES ('" 
            + front + "', '" + back  + "');";
            statement.executeUpdate(query1);
        }catch(SQLException E){
            System.out.println("SQLException:" + E.getMessage());
            System.out.println("SQLState:" + E.getSQLState());
            System.out.println("VendorError:" + E.getErrorCode());
            redirectAttributes.addFlashAttribute("errorMessage", "The flashcard already exists!");
            return "redirect:/quizMeDB/flashcard"; // Redirect to the error page with the error message
        }
        String query = "INSERT INTO flashcards (flashid, favorite, front) " + "VALUES ('" 
        + flashid + "', '" + favorite + "', '" + front  + "');";
        statement.executeUpdate(query);
        connection.close();
        redirectAttributes.addFlashAttribute("success", "Success!");
        return "redirect:/quizMeDB/flashcard";
    }
}
