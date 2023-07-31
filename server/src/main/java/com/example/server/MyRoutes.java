package com.example.server;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/quizMeDB")
public class MyRoutes {
    @GetMapping("/register")
    public String register(Model model){
        String message = "Hello from Spring Boot!";
        model.addAttribute("message", message);
        return "register";
    }
}
