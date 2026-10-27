package com.example.questionbank.api;

import com.example.questionbank.api.dto.CreateQuizRequest;
import com.example.questionbank.application.QuizService;
import com.example.questionbank.domain.model.Quiz;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Quiz management and simulation creation.
 */
@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    // =========================================================================
    // Use Case 5: Create Quiz (Guarantees only PUBLISHED questions)
    // =========================================================================

    @PostMapping
    public ResponseEntity<Quiz> createQuiz(@RequestBody CreateQuizRequest request) {
        Quiz created = quizService.createQuiz(
            request.getTitle(),
            request.getDescription(),
            request.getCreatorId(),
            request.getQuestionIds()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/{quizId}/questions/{questionId}")
    public Quiz addQuestionToQuiz(@PathVariable Long quizId, @PathVariable Long questionId) {
        return quizService.addQuestionToQuiz(quizId, questionId);
    }

    @GetMapping
    public List<Quiz> getAllQuizzes() {
        return quizService.getAllQuizzes();
    }

    @GetMapping("/{id}")
    public Quiz getQuizById(@PathVariable Long id) {
        return quizService.getQuizById(id);
    }
}
