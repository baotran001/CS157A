package com.example.server;
import java.sql.*;
import java.util.*;
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
public class QuizRoutes {
    @GetMapping("/quiz")
    public String displayQuiz(@RequestParam("sid") String sidValue, @RequestParam("name") String setName,
    @CookieValue(name = "user_uid", required = false) Cookie cookie, Model model) throws SQLException{
        
        if (cookie == null) {
            // Handle the case when the cookie is not present
            return "redirect:/quizMeDB/login"; // Redirect to the login page or an appropriate page
        }
        
        model.addAttribute("cookieName",cookie.getValue());
        
        // Retrieve all flashcards belonging to the set
        Connection connection = Utility.createSQLConnection();
        Statement statement = connection.createStatement();
        String query = 
        "Select DISTINCT F.front, FB.back FROM flashcards F, FrontHasBack FB" +
        ", sethasflashcards SF WHERE F.front = FB.front AND F.flashid = SF.flashid AND " + "SF.sid = '" + sidValue + "';";
        ResultSet res = statement.executeQuery(query);
        ArrayList<String> answers = new ArrayList<>();
        ArrayList<Question> questions = new ArrayList<>();
        while (res.next()) {
            Question q = new Question();
            q.setQuestion(res.getString("front"));
            answers.add(res.getString("back"));
            questions.add(q);
        }
        Collections.shuffle(answers);
        //model.addAttribute("dataList", flashcArr);
        for(int i = 0; i < questions.size(); i++){
            questions.get(i).setAnswer(answers.get(i));
        }
        model.addAttribute("questionsList", questions);
        return "quiz";
    }
    @PostMapping("/quiz")
    public String quizQuestions(@ModelAttribute("folder") Folder folder, RedirectAttributes redirectAttributes,
                             @CookieValue(name = "user_uid", required = false) Cookie cookie) throws SQLException{
        
        return "redirect:/quizMeDB/results";
    }
}
