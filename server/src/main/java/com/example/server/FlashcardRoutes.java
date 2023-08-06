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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/quizMeDB")
public class FlashcardRoutes {


    private String setName;

    public FlashcardRoutes() {
        this.setName = "";
    }

    public String getSetName(){
        return this.setName;
    }

    public void setSetName(String setName){
        this.setName = setName;
    }

    @GetMapping("/flashcard")
    public String displayFlashcardsPage(@RequestParam("sid") String sidValue, 
    @RequestParam("name") String setName, RedirectAttributes redirectAttributes, @CookieValue(name = "user_uid", required = false) Cookie cookie, Model model) throws SQLException{
        if(cookie != null){
            model.addAttribute("cookieName",cookie.getValue());
        }
        setSetName(setName);
        model.addAttribute("sName", getSetName());
        model.addAttribute("sidVal", sidValue);
        model.addAttribute("flashcard", new FlashCard());
        Connection connection = Utility.createSQLConnection();
        Statement statement = connection.createStatement();
        String query = 
        "Select DISTINCT F.flashid, F.favorite, F.front, FB.back FROM flashcards F, FrontHasBack FB" +
        ", sethasflashcards SF WHERE F.front = FB.front AND F.flashid = SF.flashid AND " + "SF.sid = '" + sidValue + "';";
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
        }
        model.addAttribute("dataList", flashcArr);

        // Set List
        return "flashcard";
    }
    @PostMapping("/flashcard")
    public String createFlashcard(@CookieValue(name = "user_uid", required = false) Cookie cookie, Model model, @ModelAttribute("flashcard") FlashCard flashcard, RedirectAttributes redirectAttributes) throws SQLException{
        String sidValue = flashcard.getSid();
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
            return "redirect:/quizMeDB/flashcard?sid=" + sidValue; // Redirect to the error page with the error message
        }
        String query = "INSERT INTO flashcards (flashid, favorite, front) " + "VALUES ('" 
        + flashid + "', '" + favorite + "', '" + front  + "');";
        statement.executeUpdate(query);

        String query2 = "INSERT INTO sethasflashcards (sid, flashid)" + " VALUES ('" +
        sidValue + "', '" + flashid + "');";
        statement.executeUpdate(query2);
    
        connection.close();
        redirectAttributes.addFlashAttribute("success", "Success!");
        return "redirect:/quizMeDB/flashcard?sid=" + sidValue + "&name=" + getSetName();
    }
}
