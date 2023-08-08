package com.example.server;
import java.sql.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

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
    public String quizQuestions(@RequestParam("questionList") String questionList,
                                @RequestParam("selectedAnswers") String selectedAnswers,
                                @ModelAttribute("folder") Folder folder,
                                RedirectAttributes redirectAttributes,
                                @CookieValue(name = "user_uid", required = false) Cookie cookie,  Model model) throws SQLException {

        System.out.println("QuestionList " + questionList);
        System.out.println("Selected Answers " + selectedAnswers);

        //------Creates Two ArrayList
        ArrayList<String> questions = new ArrayList<>();
        ArrayList<String> answers = new ArrayList<>();

        Pattern pattern = Pattern.compile("question='(.*?)', answer='(.*?)'");
        Matcher matcher = pattern.matcher(questionList);

        while (matcher.find()) {
            questions.add(matcher.group(1));
            answers.add(matcher.group(2));
        }

        System.out.println("Questions: " + questions);
        System.out.println("Answers: " + answers);


        //-------Creates the selected answer arrayList
        List<String> userAnswer = Arrays.asList(selectedAnswers.split(","));

        // If you need an ArrayList specifically
        ArrayList<String> userAnswerArrayList = new ArrayList<>(userAnswer);

        // Print the ArrayList
        System.out.println("The user answers: " + userAnswerArrayList);


        //Check if questions matches answers
        ArrayList<String> matches = new ArrayList<>();

        Connection connection = Utility.createSQLConnection();
        String query = "SELECT back FROM FrontHasBack WHERE front = ?";
        
        for (int i = 0; i < questions.size(); i++) {
            String question = questions.get(i);
            String answer = answers.get(i);

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, question);
                try (ResultSet resultSet = statement.executeQuery()) {
                    boolean isMatch = false;
                    while (resultSet.next()) {
                        String dbAnswer = resultSet.getString("back");
                        System.out.println("Answers: " + dbAnswer);
                        if (dbAnswer.equals(answer)) {
                            System.out.println("In true : " + answer);
                            matches.add("True");
                            isMatch = true;
                            break;
                        }
                    }
                    if (!isMatch) {
                        matches.add("False");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Matches array: " + matches);


        if (userAnswer.size() != matches.size()) {
            throw new IllegalArgumentException("ArrayLists must have the same size");
        }
        
        int totalScore = 0;
        for (int i = 0; i < userAnswer.size(); i++) {
            if (userAnswer.get(i).equals(matches.get(i))) {
                totalScore++;
            }
        }
        
        int totalQuestions = userAnswer.size();
        double percentageCorrect = ((double) totalScore / totalQuestions) * 100;
        
        System.out.println("Total Score: " + totalScore);
        System.out.println("Total Questions: " + totalQuestions);
        System.out.println("Percentage Correct: " + percentageCorrect + "%");


       

        return results(totalScore, totalQuestions, percentageCorrect, folder,  redirectAttributes, cookie, model);
    }

     @GetMapping("/result")
     public String results(@RequestParam("totalScore") int totalScore,
                                @RequestParam("totalQuestions")int totalQuestions,
                                @RequestParam("percentageCorrect") Double percentageCorrect,
                                @ModelAttribute("folder") Folder folder,
                                RedirectAttributes redirectAttributes,
                                @CookieValue(name = "user_uid", required = false) Cookie cookie, Model model) throws SQLException {
                                     
         if (cookie == null) {
            // Handle the case when the cookie is not present
            return "redirect:/quizMeDB/login"; // Redirect to the login page or an appropriate page
        }
        model.addAttribute("totalScore", totalScore);
        model.addAttribute("totalQuestions", totalQuestions);
        model.addAttribute("percentageCorrect", percentageCorrect);
        return "result";
    }
}
