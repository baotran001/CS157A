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
    private String sid;

    public FlashcardRoutes() {
        this.setName = "";
        this.sid = "";
    }

    public String getSetName(){
        return this.setName;
    }

    public void setSetName(String setName){
        this.setName = setName;
    }

    public String getSid(){
        return this.sid;
    }

    public void setSid(String sid){
        this.sid = sid;
    }

    @GetMapping("/flashcard")
    public String displayFlashcardsPage(@RequestParam("sid") String sidValue, 
    @RequestParam("name") String setName, RedirectAttributes redirectAttributes, @CookieValue(name = "user_uid", required = false) Cookie cookie, Model model) throws SQLException{
        if(cookie != null){
            model.addAttribute("cookieName",cookie.getValue());
        }
        setSetName(setName);
        setSid(sidValue);
        model.addAttribute("sName", getSetName());
        model.addAttribute("sidVal", getSid());
        model.addAttribute("flashcard", new FlashCard());
        model.addAttribute("review", new Review());
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

        String query1 = "SELECT DISTINCT R.rid, R.star, R.author, R.date, R.text FROM Reviews R, setshasreviews SR, userwritesreviews UR " + 
        "WHERE R.rid = SR.rid AND R.rid = UR.rid AND SR.sid = '" + getSid() + "' AND UR.uid = '" + cookie.getValue() + "';";
        ResultSet res1 = statement.executeQuery(query1);
        ArrayList<Review> reviewArr = new ArrayList<>();
        while (res1.next()) {
            String rid = res1.getString("rid");
            int star = res1.getInt("star");
            String author = res1.getString("author");
            java.sql.Date date = res1.getDate("date");
            String text = res1.getString("text");

            Review review = new Review();
            review.setRid(rid);
            review.setStar(star);
            review.setAuthor(author);
            review.setDate(date);
            review.setText(text);
            reviewArr.add(review);
        }
        model.addAttribute("reviewList", reviewArr);
        connection.close();
        return "flashcard";
    }
    @PostMapping("/flashcard")
    public String createFlashcard(@CookieValue(name = "user_uid", required = false) Cookie cookie, Model model, @ModelAttribute("flashcard") FlashCard flashcard, RedirectAttributes redirectAttributes) throws SQLException{
        String sidValue = getSid();
        String flashid = flashcard.getFlashid();
        String favorite = flashcard.getFavorite();
        String front = flashcard.getFront();
        String back = flashcard.getBack();
        Connection connection = Utility.createSQLConnection();
        Statement statement = connection.createStatement();
        String query = "SELECT COUNT(*) FROM FrontHasBack WHERE front = '" + front + "' AND back = '" + back + "';";
        int count = 0;
        ResultSet rs = statement.executeQuery(query);
        if (rs.next()) {
            count = rs.getInt("Count(*)");
        }
        if (count == 0){
            String query1 = "INSERT INTO FrontHasBack (front, back) " + "VALUES ('" 
            + front + "', '" + back  + "');";
            statement.executeUpdate(query1);
        }
        String query2 = "INSERT INTO flashcards (flashid, favorite, front) " + "VALUES ('" 
        + flashid + "', '" + favorite + "', '" + front  + "');";
        statement.executeUpdate(query2);

        String query3 = "INSERT INTO sethasflashcards (sid, flashid)" + " VALUES ('" +
        sidValue + "', '" + flashid + "');";
        statement.executeUpdate(query3);
    
        connection.close();
        redirectAttributes.addFlashAttribute("success", "Success!");
        return "redirect:/quizMeDB/flashcard?sid=" + getSid() + "&name=" + getSetName();
    }

    @PostMapping("/createReview")
    public String createFlashcard(RedirectAttributes redirectAttributes, 
    @ModelAttribute("review") Review review, @CookieValue(name = "user_uid", required = false) Cookie cookie, Model model) throws SQLException{
        String rid = review.getRid();
        int star = review.getStar();
        String author = cookie.getValue();
        java.sql.Date date = review.getDate();
        String text = review.getText();

        Connection connection = Utility.createSQLConnection();
        Statement statement = connection.createStatement();
        String query1 = "INSERT INTO Reviews (rid, star, author, date, text) " + "VALUES ('" 
        + rid + "', " + star + ", '" + author  + "', '" + date + "', '" + text + "');";
        statement.executeUpdate(query1);

        String query2 = "INSERT INTO setshasreviews (sid, rid) " + "VALUES ('" +
        getSid() + "', '" + rid + "');";
        statement.executeUpdate(query2);
        
        String query3 = "INSERT INTO userwritesreviews (uid, rid) " + "VALUES ('" +
        cookie.getValue() + "', '" + rid + "');";
        statement.executeUpdate(query3);

        redirectAttributes.addFlashAttribute("success", "Review Created!");
        return "redirect:/quizMeDB/flashcard?sid=" + getSid() + "&name=" + getSetName();
    }
}
