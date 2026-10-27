package com.example.questionbank.api;

import com.example.questionbank.api.dto.*;
import com.example.questionbank.application.QuestionService;
import com.example.questionbank.domain.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * REST controller exposing endpoints for Question management and lifecycle workflow.
 */
@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    // =========================================================================
    // Use Case 1: Create Question
    // =========================================================================

    @PostMapping("/multiple-choice")
    public ResponseEntity<MultipleChoiceQuestion> createMultipleChoice(
            @RequestBody CreateMultipleChoiceQuestionRequest request) {

        List<AnswerOption> options = new ArrayList<>();
        if (request.getOptions() != null) {
            for (AnswerOptionDto dto : request.getOptions()) {
                options.add(new AnswerOption(null, dto.getText(), dto.getExplanation(), dto.isCorrect()));
            }
        }

        QuestionMetadata metadata = null;
        if (request.getMetadata() != null) {
            metadata = new QuestionMetadata(
                null,
                request.getMetadata().getCognitiveLevel(),
                request.getMetadata().getEstimatedTimeSeconds(),
                request.getMetadata().getEditorialNotes(),
                null
            );
        }

        MultipleChoiceQuestion created = questionService.createMultipleChoiceQuestion(
            request.getStatement(),
            request.getDifficulty(),
            request.getAuthorId(),
            request.getTopicId(),
            options,
            metadata,
            request.getCompetencyIds()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/true-false")
    public ResponseEntity<TrueFalseQuestion> createTrueFalse(
            @RequestBody CreateTrueFalseQuestionRequest request) {

        QuestionMetadata metadata = null;
        if (request.getMetadata() != null) {
            metadata = new QuestionMetadata(
                null,
                request.getMetadata().getCognitiveLevel(),
                request.getMetadata().getEstimatedTimeSeconds(),
                request.getMetadata().getEditorialNotes(),
                null
            );
        }

        TrueFalseQuestion created = questionService.createTrueFalseQuestion(
            request.getStatement(),
            request.getDifficulty(),
            request.getAuthorId(),
            request.getTopicId(),
            request.getExpectedValue(),
            request.getJustification(),
            metadata,
            request.getCompetencyIds()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // =========================================================================
    // Use Case 2: Query Questions
    // =========================================================================

    @GetMapping
    public List<Question> getAllQuestions() {
        return questionService.getAllQuestions();
    }

    @GetMapping("/{id}")
    public Question getQuestionById(@PathVariable Long id) {
        return questionService.getQuestionById(id);
    }

    @GetMapping("/author/{authorId}")
    public List<Question> getQuestionsByAuthor(@PathVariable Long authorId) {
        return questionService.getQuestionsByAuthor(authorId);
    }

    @GetMapping("/status/{status}")
    public List<Question> getQuestionsByStatus(@PathVariable QuestionStatus status) {
        return questionService.getQuestionsByStatus(status);
    }

    @GetMapping("/competency/{competencyId}/published")
    public List<Question> getPublishedByCompetency(@PathVariable Long competencyId) {
        return questionService.getPublishedQuestionsByCompetency(competencyId);
    }

    // =========================================================================
    // Use Case 3: Classify Question
    // =========================================================================

    @PutMapping("/{id}/classify")
    public Question classifyQuestion(@PathVariable Long id, @RequestBody ClassifyQuestionRequest request) {
        return questionService.classifyQuestion(id, request.getTopicId(), request.getCompetencyIds());
    }

    // =========================================================================
    // Use Case 4: Submit Question for Review (DRAFT -> PENDING_REVIEW)
    // =========================================================================

    @PostMapping("/{id}/submit-review")
    public Question submitForReview(@PathVariable Long id) {
        return questionService.submitForReview(id);
    }

    // =========================================================================
    // Review and Publication Workflow
    // =========================================================================

    @PostMapping("/{id}/start-review")
    public Question startReview(@PathVariable Long id) {
        return questionService.startReview(id);
    }

    @PostMapping("/{id}/approve")
    public Question approveQuestion(@PathVariable Long id) {
        return questionService.approveQuestion(id);
    }

    @PostMapping("/{id}/reject")
    public Question rejectQuestion(@PathVariable Long id, @RequestBody(required = false) ReviewFeedbackRequest request) {
        String feedback = request != null ? request.getFeedback() : "Rejected during review.";
        return questionService.rejectQuestion(id, feedback);
    }

    @PostMapping("/{id}/publish")
    public Question publishQuestion(@PathVariable Long id) {
        return questionService.publishQuestion(id);
    }
}
