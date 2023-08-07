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
public class QuizRoutes {
    @GetMapping("/quiz")
    public String displayQuiz(@CookieValue(name = "user_uid", required = false) Cookie cookie, Model model) throws SQLException{
        
        if (cookie == null) {
            // Handle the case when the cookie is not present
            return "redirect:/quizMeDB/login"; // Redirect to the login page or an appropriate page
        }
        
        model.addAttribute("cookieName",cookie.getValue());
        return "quiz";
    }
    @PostMapping("/quiz")
    public String quizQuestions(@ModelAttribute("folder") Folder folder, RedirectAttributes redirectAttributes,
                             @CookieValue(name = "user_uid", required = false) Cookie cookie) throws SQLException{
        
        return "redirect:/quizMeDB/results";
    }
}
