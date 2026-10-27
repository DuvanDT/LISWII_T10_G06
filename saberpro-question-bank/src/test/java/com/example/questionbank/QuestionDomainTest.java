package com.example.questionbank;

import com.example.questionbank.domain.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pure domain unit tests verifying business rules and lifecycle invariants.
 * Executes without any Spring Boot context or database dependencies.
 */
class QuestionDomainTest {

    @Test
    @DisplayName("Should successfully transition question lifecycle: DRAFT -> PENDING_REVIEW -> IN_REVIEW -> APPROVED -> PUBLISHED")
    void testCompleteSuccessfulLifecycle() {
        MultipleChoiceQuestion question = new MultipleChoiceQuestion(
            1L,
            "What is the primary benefit of the Clean Persistence pattern?",
            DifficultyLevel.MEDIUM,
            QuestionStatus.DRAFT,
            1L,
            1L
        );

        // Add 5 options: 4 distractors + 1 correct answer
        question.addOption(new AnswerOption(1L, "Tightly couples entities with SQL tables", "Incorrect", false));
        question.addOption(new AnswerOption(2L, "Prevents database contamination in domain models", "Correct rationale", true));
        question.addOption(new AnswerOption(3L, "Eliminates the need for repository interfaces", "Incorrect", false));
        question.addOption(new AnswerOption(4L, "Forces the use of hexagonal port packages", "Incorrect", false));
        question.addOption(new AnswerOption(5L, "Replaces relational models with XML storage", "Incorrect", false));

        // Add metadata and competencies
        question.setMetadata(new QuestionMetadata(1L, "Evaluation", 120, "Pedagogically verified", LocalDate.now()));
        question.addCompetencyId(1L);

        // 1. DRAFT -> PENDING_REVIEW
        question.submitForReview();
        assertEquals(QuestionStatus.PENDING_REVIEW, question.getStatus());

        // 2. PENDING_REVIEW -> IN_REVIEW
        question.startReview();
        assertEquals(QuestionStatus.IN_REVIEW, question.getStatus());

        // 3. IN_REVIEW -> APPROVED
        question.approve();
        assertEquals(QuestionStatus.APPROVED, question.getStatus());

        // 4. APPROVED -> PUBLISHED
        question.publish();
        assertEquals(QuestionStatus.PUBLISHED, question.getStatus());
    }

    @Test
    @DisplayName("Should reject submission to review when multiple-choice question has zero correct answers")
    void testSubmissionFailsWhenNoCorrectOption() {
        MultipleChoiceQuestion question = new MultipleChoiceQuestion(
            2L, "Sample question", DifficultyLevel.EASY, QuestionStatus.DRAFT, 1L, 1L
        );
        question.addOption(new AnswerOption(1L, "Opt 1", "desc", false));
        question.addOption(new AnswerOption(2L, "Opt 2", "desc", false));
        question.setMetadata(new QuestionMetadata(1L, "Knowledge", 60, "notes", LocalDate.now()));
        question.addCompetencyId(1L);

        IllegalStateException ex = assertThrows(IllegalStateException.class, question::submitForReview);
        assertTrue(ex.getMessage().contains("ONE correct option"));
    }

    @Test
    @DisplayName("Should reject submission when multiple-choice question has more than one correct answer")
    void testSubmissionFailsWhenMultipleCorrectOptions() {
        MultipleChoiceQuestion question = new MultipleChoiceQuestion(
            3L, "Sample question", DifficultyLevel.HARD, QuestionStatus.DRAFT, 1L, 1L
        );
        question.addOption(new AnswerOption(1L, "Opt 1", "desc", true));
        question.addOption(new AnswerOption(2L, "Opt 2", "desc", true));
        question.setMetadata(new QuestionMetadata(1L, "Synthesis", 90, "notes", LocalDate.now()));
        question.addCompetencyId(1L);

        IllegalStateException ex = assertThrows(IllegalStateException.class, question::submitForReview);
        assertTrue(ex.getMessage().contains("ONE correct option"));
    }

    @Test
    @DisplayName("Should support reviewer rejection returning question back to DRAFT with feedback")
    void testReviewerRejectionWorkflow() {
        TrueFalseQuestion question = new TrueFalseQuestion(
            4L,
            "Domain classes must always import jakarta.persistence annotations.",
            DifficultyLevel.EASY,
            QuestionStatus.DRAFT,
            1L,
            1L,
            false,
            "Clean persistence strictly forbids technological annotations in domain."
        );
        question.setMetadata(new QuestionMetadata(1L, "Comprehension", 45, "Initial notes", LocalDate.now()));
        question.addCompetencyId(1L);

        question.submitForReview();
        question.startReview();
        assertEquals(QuestionStatus.IN_REVIEW, question.getStatus());

        // Rejection
        question.reject("Statement wording is ambiguous; please clarify domain boundary context.");
        assertEquals(QuestionStatus.DRAFT, question.getStatus());
        assertEquals("Statement wording is ambiguous; please clarify domain boundary context.",
                     question.getMetadata().getEditorialNotes());
    }

    @Test
    @DisplayName("Should fail when True/False question lacks expected truth value")
    void testTrueFalseFailsWithoutExpectedValue() {
        TrueFalseQuestion question = new TrueFalseQuestion(
            5L, "JPA handles ORM.", DifficultyLevel.EASY, QuestionStatus.DRAFT, 1L, 1L, null, null
        );
        question.setMetadata(new QuestionMetadata(1L, "Recall", 30, "notes", LocalDate.now()));
        question.addCompetencyId(1L);

        IllegalStateException ex = assertThrows(IllegalStateException.class, question::submitForReview);
        assertTrue(ex.getMessage().contains("expected boolean value"));
    }
}
