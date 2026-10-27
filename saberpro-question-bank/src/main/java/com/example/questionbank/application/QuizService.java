package com.example.questionbank.application;

import com.example.questionbank.domain.model.Question;
import com.example.questionbank.domain.model.QuestionStatus;
import com.example.questionbank.domain.model.Quiz;
import com.example.questionbank.domain.model.User;
import com.example.questionbank.domain.model.UserRole;
import com.example.questionbank.domain.repository.QuestionRepository;
import com.example.questionbank.domain.repository.QuizRepository;
import com.example.questionbank.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Application service for Quiz management and simulation creation.
 * Enforces business rule: ONLY PUBLISHED questions may be included in quizzes.
 */
@Service
@Transactional
public class QuizService {

    private final QuizRepository quizzes;
    private final QuestionRepository questions;
    private final UserRepository users;

    public QuizService(QuizRepository quizzes, QuestionRepository questions, UserRepository users) {
        this.quizzes = quizzes;
        this.questions = questions;
        this.users = users;
    }

    // =========================================================================
    // Use Case 5: Create a Quiz with Published Questions
    // =========================================================================

    /**
     * Creates a new quiz, guaranteeing only PUBLISHED questions are attached.
     *
     * @param title quiz title
     * @param description test instructions
     * @param creatorId teacher or admin creator ID
     * @param questionIds list of question IDs to attach
     * @return created Quiz
     */
    public Quiz createQuiz(String title, String description, Long creatorId, List<Long> questionIds) {
        validateCreator(creatorId);

        Quiz quiz = new Quiz(null, title, description, creatorId);

        if (questionIds != null) {
            for (Long qId : questionIds) {
                Question question = questions.findById(qId)
                    .orElseThrow(() -> new IllegalArgumentException("Question not found with ID: " + qId));

                // Business Rule Invariant: Only PUBLISHED questions can be incorporated in quizzes
                if (question.getStatus() != QuestionStatus.PUBLISHED) {
                    throw new IllegalStateException(
                        "Cannot add question ID " + qId + " to quiz: Only PUBLISHED questions are allowed. Current status is " + question.getStatus()
                    );
                }
                quiz.addQuestionId(qId);
            }
        }

        return quizzes.save(quiz);
    }

    /**
     * Adds an additional published question to an existing quiz.
     *
     * @param quizId quiz ID
     * @param questionId question ID
     * @return updated Quiz
     */
    public Quiz addQuestionToQuiz(Long quizId, Long questionId) {
        Quiz quiz = getQuizById(quizId);
        Question question = questions.findById(questionId)
            .orElseThrow(() -> new IllegalArgumentException("Question not found with ID: " + questionId));

        if (question.getStatus() != QuestionStatus.PUBLISHED) {
            throw new IllegalStateException(
                "Cannot add question ID " + questionId + " to quiz: Question is not PUBLISHED (Status: " + question.getStatus() + ")."
            );
        }

        quiz.addQuestionId(questionId);
        return quizzes.save(quiz);
    }

    @Transactional(readOnly = true)
    public Quiz getQuizById(Long id) {
        return quizzes.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Quiz not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Quiz> getAllQuizzes() {
        return quizzes.findAll();
    }

    private void validateCreator(Long creatorId) {
        if (creatorId == null) {
            throw new IllegalArgumentException("Creator ID must not be null.");
        }
        User user = users.findById(creatorId)
            .orElseThrow(() -> new IllegalArgumentException("Creator not found with ID: " + creatorId));
        if (user.getRole() != UserRole.TEACHER && user.getRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("User with ID " + creatorId + " is not authorized as TEACHER or ADMIN.");
        }
    }
}
