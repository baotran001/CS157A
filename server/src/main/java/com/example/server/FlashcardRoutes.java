package com.example.server;
import java.sql.*;
import java.util.*;
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
            setSetName(setName);
            setSid(sidValue);
            model.addAttribute("sName", getSetName());
            model.addAttribute("sidVal", getSid());
            model.addAttribute("flashcard", new FlashCard());
            model.addAttribute("review", new Review());
            model.addAttribute("comment", new Comment());
            Connection connection = Utility.createSQLConnection();
            Statement statement = connection.createStatement();
            String query = 
            "Select DISTINCT F.flashid, F.favorite, F.front, FB.back FROM flashcards F, FrontHasBack FB" +
            ", sethasflashcards SF WHERE F.front = FB.front AND F.flashid = SF.flashid AND " + "SF.sid = '" + sidValue + "';";
            ResultSet res = statement.executeQuery(query);
            HashSet<FlashCard> flashcArr = new HashSet<>();
            while (res.next()) {
                FlashCard flashc = new FlashCard();
                flashc.setFlashid(res.getString("flashid"));
                flashc.setFavorite(res.getString("favorite"));
                flashc.setFront(res.getString("front"));
                flashc.setBack(res.getString("back"));
                flashcArr.add(flashc);
            }
            model.addAttribute("dataList", flashcArr);

            String query1 = "SELECT DISTINCT R.rid, R.star, R.author, R.date, R.text FROM Reviews R, setshasreviews SR, userwritesreviews UR " + 
            "WHERE R.rid = SR.rid AND R.rid = UR.rid AND SR.sid = '" + getSid() + "' AND UR.uid = '" + cookie.getValue() + "';";
            ResultSet res1 = statement.executeQuery(query1);
            ArrayList<Review> reviewArr = new ArrayList<>();
            while (res1.next()) {
                Review review = new Review();
                review.setRid(res1.getString("rid"));
                review.setStar(res1.getInt("star"));
                review.setAuthor(res1.getString("author"));
                review.setDate(res1.getDate("date"));
                review.setText(res1.getString("text"));
                reviewArr.add(review);
            }
            model.addAttribute("reviewList", reviewArr);
            for(Review review: reviewArr){
                String query2 = "SELECT RHC.rid, RHC.cid," +
                "RHC.date, RHC.author, RHC.text FROM reviewshascomments RHC, Reviews R " + 
                "WHERE R.rid = RHC.rid AND R.rid = '" + review.getRid() + "';";
                ResultSet res2 = statement.executeQuery(query2);
                while(res2.next()){
                    Comment comment = new Comment();
                    comment.setCid(res2.getString("cid"));
                    comment.setAuthor(res2.getString("author"));
                    comment.setDate(res2.getDate("date"));
                    comment.setText(res2.getString("text"));
                    review.addComment(comment);
                }
        }

        connection.close();
        }
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
    public String createReview(RedirectAttributes redirectAttributes, 
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
        connection.close();
        redirectAttributes.addFlashAttribute("success", "Review Created!");
        return "redirect:/quizMeDB/flashcard?sid=" + getSid() + "&name=" + getSetName();
    }

    @PostMapping("/createComment")
    public String createComment(RedirectAttributes redirectAttributes, 
    @ModelAttribute("comment") Comment comment, @RequestParam("ridField") String ridValue,
    @CookieValue(name = "user_uid", required = false) Cookie cookie, Model model) throws SQLException{
        String rid = ridValue;
        String cid = comment.getCid();
        String author = cookie.getValue();
        java.sql.Date date = comment.getDate();
        String text = comment.getText();

        Connection connection = Utility.createSQLConnection();
        Statement statement = connection.createStatement();
        String query1 = "INSERT INTO reviewshascomments (rid, cid, date, author, text) " + "VALUES ('" 
        + rid + "', '" + cid + "', '" + date  + "', '" + author + "', '" + text + "');";
        statement.executeUpdate(query1);

        connection.close();
        return "redirect:/quizMeDB/flashcard?sid=" + getSid() + "&name=" + getSetName();
    }
}
