package com.example.questionbank.application;

import com.example.questionbank.domain.model.*;
import com.example.questionbank.domain.repository.CompetencyRepository;
import com.example.questionbank.domain.repository.QuestionRepository;
import com.example.questionbank.domain.repository.TopicRepository;
import com.example.questionbank.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Application service for Question management and lifecycle orchestration.
 * Depends strictly on domain repository contracts, not on JPA or Spring Data.
 */
@Service
@Transactional
public class QuestionService {

    private final QuestionRepository questions;
    private final UserRepository users;
    private final TopicRepository topics;
    private final CompetencyRepository competencies;

    public QuestionService(QuestionRepository questions,
                           UserRepository users,
                           TopicRepository topics,
                           CompetencyRepository competencies) {
        this.questions = questions;
        this.users = users;
        this.topics = topics;
        this.competencies = competencies;
    }

    // =========================================================================
    // Use Case 1: Create a Question (Multiple Choice or True/False)
    // =========================================================================

    /**
     * Creates a new multiple-choice question.
     *
     * @param statement question text
     * @param difficulty difficulty level
     * @param authorId author user ID
     * @param topicId topic ID
     * @param options list of answer choices
     * @param metadata question metadata
     * @param competencyIds list of associated competency IDs
     * @return created question in DRAFT status
     */
    public MultipleChoiceQuestion createMultipleChoiceQuestion(String statement,
                                                               DifficultyLevel difficulty,
                                                               Long authorId,
                                                               Long topicId,
                                                               List<AnswerOption> options,
                                                               QuestionMetadata metadata,
                                                               List<Long> competencyIds) {
        validateAuthor(authorId);
        validateTopic(topicId);

        MultipleChoiceQuestion question = new MultipleChoiceQuestion(
            null, statement, difficulty, QuestionStatus.DRAFT, authorId, topicId
        );
        if (options != null) {
            options.forEach(question::addOption);
        }
        if (metadata != null) {
            metadata.setLastReviewedAt(LocalDate.now());
            question.setMetadata(metadata);
        }
        if (competencyIds != null) {
            competencyIds.forEach(question::addCompetencyId);
        }

        return (MultipleChoiceQuestion) questions.save(question);
    }

    /**
     * Creates a new true/false question.
     *
     * @param statement question text
     * @param difficulty difficulty level
     * @param authorId author user ID
     * @param topicId topic ID
     * @param expectedValue expected truth value (true/false)
     * @param justification explanation of correct value
     * @param metadata question metadata
     * @param competencyIds list of associated competency IDs
     * @return created question in DRAFT status
     */
    public TrueFalseQuestion createTrueFalseQuestion(String statement,
                                                     DifficultyLevel difficulty,
                                                     Long authorId,
                                                     Long topicId,
                                                     Boolean expectedValue,
                                                     String justification,
                                                     QuestionMetadata metadata,
                                                     List<Long> competencyIds) {
        validateAuthor(authorId);
        validateTopic(topicId);

        TrueFalseQuestion question = new TrueFalseQuestion(
            null, statement, difficulty, QuestionStatus.DRAFT, authorId, topicId,
            expectedValue, justification
        );
        if (metadata != null) {
            metadata.setLastReviewedAt(LocalDate.now());
            question.setMetadata(metadata);
        }
        if (competencyIds != null) {
            competencyIds.forEach(question::addCompetencyId);
        }

        return (TrueFalseQuestion) questions.save(question);
    }

    // =========================================================================
    // Use Case 2: Query Questions
    // =========================================================================

    @Transactional(readOnly = true)
    public Question getQuestionById(Long id) {
        return questions.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Question not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Question> getAllQuestions() {
        return questions.findAll();
    }

    @Transactional(readOnly = true)
    public List<Question> getQuestionsByAuthor(Long authorId) {
        return questions.findByAuthorId(authorId);
    }

    @Transactional(readOnly = true)
    public List<Question> getQuestionsByStatus(QuestionStatus status) {
        return questions.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Question> getPublishedQuestionsByCompetency(Long competencyId) {
        return questions.findPublishedByCompetencyId(competencyId);
    }

    // =========================================================================
    // Use Case 3: Classify a Question (Competencies and Topic)
    // =========================================================================

    /**
     * Classifies a question by updating its assigned topic and associated competencies.
     *
     * @param questionId identifier of question to classify
     * @param topicId new topic ID
     * @param competencyIds list of competency IDs
     * @return updated Question
     */
    public Question classifyQuestion(Long questionId, Long topicId, List<Long> competencyIds) {
        Question question = getQuestionById(questionId);

        if (topicId != null) {
            validateTopic(topicId);
            question.setTopicId(topicId);
        }

        if (competencyIds != null) {
            for (Long cId : competencyIds) {
                competencies.findById(cId)
                    .orElseThrow(() -> new IllegalArgumentException("Competency not found with ID: " + cId));
            }
            question.setCompetencyIds(competencyIds);
        }

        return questions.save(question);
    }

    // =========================================================================
    // Use Case 4: Submit Question for Review (DRAFT -> PENDING_REVIEW)
    // =========================================================================

    /**
     * Validates business invariants and transitions question from DRAFT to PENDING_REVIEW.
     *
     * @param questionId question identifier
     * @return updated question
     */
    public Question submitForReview(Long questionId) {
        Question question = getQuestionById(questionId);
        question.submitForReview(); // Executes domain invariants
        return questions.save(question);
    }

    // =========================================================================
    // Review Workflow Methods
    // =========================================================================

    public Question startReview(Long questionId) {
        Question question = getQuestionById(questionId);
        question.startReview();
        return questions.save(question);
    }

    public Question approveQuestion(Long questionId) {
        Question question = getQuestionById(questionId);
        question.approve();
        return questions.save(question);
    }

    public Question rejectQuestion(Long questionId, String feedback) {
        Question question = getQuestionById(questionId);
        question.reject(feedback);
        return questions.save(question);
    }

    public Question publishQuestion(Long questionId) {
        Question question = getQuestionById(questionId);
        question.publish();
        return questions.save(question);
    }

    // =========================================================================
    // Helper Validations
    // =========================================================================

    private void validateAuthor(Long authorId) {
        if (authorId == null) {
            throw new IllegalArgumentException("Author ID must not be null.");
        }
        User user = users.findById(authorId)
            .orElseThrow(() -> new IllegalArgumentException("Author not found with ID: " + authorId));
        if (user.getRole() != UserRole.AUTHOR && user.getRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("User with ID " + authorId + " is not authorized as an AUTHOR.");
        }
    }

    private void validateTopic(Long topicId) {
        if (topicId == null) {
            throw new IllegalArgumentException("Topic ID must not be null.");
        }
        topics.findById(topicId)
            .orElseThrow(() -> new IllegalArgumentException("Topic not found with ID: " + topicId));
    }
}
