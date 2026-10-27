package com.example.questionbank.domain.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Pure abstract domain entity for all question types.
 * Free of persistence annotations and independent of technological frameworks.
 */
public abstract class Question {
    private Long id;
    private String statement;
    private DifficultyLevel difficulty;
    private QuestionStatus status = QuestionStatus.DRAFT;
    private Long authorId;
    private Long topicId;
    private QuestionMetadata metadata;
    private final List<Long> competencyIds = new ArrayList<>();

    protected Question() {}

    /**
     * Initializes a question with core fields.
     *
     * @param id question identifier
     * @param statement text of the question
     * @param difficulty difficulty level
     * @param status lifecycle state
     * @param authorId identifier of authoring user
     * @param topicId identifier of academic topic
     */
    protected Question(Long id, String statement, DifficultyLevel difficulty, QuestionStatus status,
                       Long authorId, Long topicId) {
        this.id = id;
        this.statement = statement;
        this.difficulty = difficulty;
        this.status = status != null ? status : QuestionStatus.DRAFT;
        this.authorId = authorId;
        this.topicId = topicId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStatement() { return statement; }
    public void setStatement(String statement) { this.statement = statement; }

    public DifficultyLevel getDifficulty() { return difficulty; }
    public void setDifficulty(DifficultyLevel difficulty) { this.difficulty = difficulty; }

    public QuestionStatus getStatus() { return status; }
    public void setStatus(QuestionStatus status) { this.status = status; }

    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }

    public Long getTopicId() { return topicId; }
    public void setTopicId(Long topicId) { this.topicId = topicId; }

    public QuestionMetadata getMetadata() { return metadata; }
    public void setMetadata(QuestionMetadata metadata) { this.metadata = metadata; }

    public List<Long> getCompetencyIds() { return List.copyOf(competencyIds); }

    /**
     * Adds a competency ID without duplicate entries.
     *
     * @param competencyId ID of the competency
     */
    public void addCompetencyId(Long competencyId) {
        if (competencyId != null && !competencyIds.contains(competencyId)) {
            competencyIds.add(competencyId);
        }
    }

    /**
     * Replaces the competency IDs.
     *
     * @param ids collection of competency IDs
     */
    public void setCompetencyIds(List<Long> ids) {
        this.competencyIds.clear();
        if (ids != null) {
            ids.forEach(this::addCompetencyId);
        }
    }

    /**
     * Business validation before submitting to review.
     */
    public abstract void validateForReview();

    /**
     * Transitions state from DRAFT to PENDING_REVIEW after verifying invariants.
     */
    public void submitForReview() {
        if (this.status != QuestionStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT questions can be submitted for review. Current status: " + this.status);
        }
        if (statement == null || statement.trim().isEmpty()) {
            throw new IllegalStateException("Question statement cannot be empty.");
        }
        if (topicId == null) {
            throw new IllegalStateException("A topic must be assigned before submitting for review.");
        }
        if (competencyIds.isEmpty()) {
            throw new IllegalStateException("At least one competency must be assigned before review.");
        }
        if (metadata == null) {
            throw new IllegalStateException("Question metadata must be completed before submitting for review.");
        }
        validateForReview();
        this.status = QuestionStatus.PENDING_REVIEW;
    }

    /**
     * Transitions state from PENDING_REVIEW to IN_REVIEW.
     */
    public void startReview() {
        if (this.status != QuestionStatus.PENDING_REVIEW) {
            throw new IllegalStateException("Question must be in PENDING_REVIEW status to start review. Current: " + this.status);
        }
        this.status = QuestionStatus.IN_REVIEW;
    }

    /**
     * Transitions state from IN_REVIEW to APPROVED.
     */
    public void approve() {
        if (this.status != QuestionStatus.IN_REVIEW) {
            throw new IllegalStateException("Question must be IN_REVIEW to be approved. Current: " + this.status);
        }
        this.status = QuestionStatus.APPROVED;
    }

    /**
     * Rejects an in-review question back to DRAFT for correction.
     *
     * @param feedback reviewer feedback notes
     */
    public void reject(String feedback) {
        if (this.status != QuestionStatus.IN_REVIEW) {
            throw new IllegalStateException("Question must be IN_REVIEW to be rejected. Current: " + this.status);
        }
        if (this.metadata != null && feedback != null) {
            this.metadata.setEditorialNotes(feedback);
        }
        this.status = QuestionStatus.DRAFT;
    }

    /**
     * Publishes an approved question.
     */
    public void publish() {
        if (this.status != QuestionStatus.APPROVED) {
            throw new IllegalStateException("Only APPROVED questions can be published. Current: " + this.status);
        }
        this.status = QuestionStatus.PUBLISHED;
    }

    /**
     * Archives a question.
     */
    public void archive() {
        this.status = QuestionStatus.ARCHIVED;
    }
}
