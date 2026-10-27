package com.example.questionbank.domain.model;

import java.time.LocalDate;

/**
 * Pure domain representation of question metadata (One-to-One relationship with Question).
 */
public class QuestionMetadata {
    private Long id;
    private String cognitiveLevel;
    private Integer estimatedTimeSeconds;
    private String editorialNotes;
    private LocalDate lastReviewedAt;

    public QuestionMetadata() {}

    /**
     * Creates new question metadata.
     *
     * @param id identifier
     * @param cognitiveLevel Bloom cognitive level (e.g., "Analysis", "Evaluation")
     * @param estimatedTimeSeconds expected response time in seconds
     * @param editorialNotes recommendations or reviewer remarks
     * @param lastReviewedAt date when last review took place
     */
    public QuestionMetadata(Long id, String cognitiveLevel, Integer estimatedTimeSeconds,
                            String editorialNotes, LocalDate lastReviewedAt) {
        this.id = id;
        this.cognitiveLevel = cognitiveLevel;
        this.estimatedTimeSeconds = estimatedTimeSeconds;
        this.editorialNotes = editorialNotes;
        this.lastReviewedAt = lastReviewedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCognitiveLevel() { return cognitiveLevel; }
    public void setCognitiveLevel(String cognitiveLevel) { this.cognitiveLevel = cognitiveLevel; }

    public Integer getEstimatedTimeSeconds() { return estimatedTimeSeconds; }
    public void setEstimatedTimeSeconds(Integer estimatedTimeSeconds) { this.estimatedTimeSeconds = estimatedTimeSeconds; }

    public String getEditorialNotes() { return editorialNotes; }
    public void setEditorialNotes(String editorialNotes) { this.editorialNotes = editorialNotes; }

    public LocalDate getLastReviewedAt() { return lastReviewedAt; }
    public void setLastReviewedAt(LocalDate lastReviewedAt) { this.lastReviewedAt = lastReviewedAt; }
}
