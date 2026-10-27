package com.example.questionbank.infrastructure.jpa.record;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * JPA entity record for question metadata (One-to-One owning side).
 */
@Entity
@Table(name = "question_metadata")
public class QuestionMetadataRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false, unique = true)
    private QuestionRecord question;

    @Column(name = "cognitive_level", length = 50)
    private String cognitiveLevel;

    @Column(name = "estimated_time_seconds")
    private Integer estimatedTimeSeconds;

    @Column(name = "editorial_notes", length = 1000)
    private String editorialNotes;

    @Column(name = "last_reviewed_at")
    private LocalDate lastReviewedAt;

    protected QuestionMetadataRecord() {}

    public QuestionMetadataRecord(String cognitiveLevel, Integer estimatedTimeSeconds,
                                  String editorialNotes, LocalDate lastReviewedAt) {
        this.cognitiveLevel = cognitiveLevel;
        this.estimatedTimeSeconds = estimatedTimeSeconds;
        this.editorialNotes = editorialNotes;
        this.lastReviewedAt = lastReviewedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public QuestionRecord getQuestion() { return question; }
    public void setQuestion(QuestionRecord question) { this.question = question; }

    public String getCognitiveLevel() { return cognitiveLevel; }
    public void setCognitiveLevel(String cognitiveLevel) { this.cognitiveLevel = cognitiveLevel; }

    public Integer getEstimatedTimeSeconds() { return estimatedTimeSeconds; }
    public void setEstimatedTimeSeconds(Integer estimatedTimeSeconds) { this.estimatedTimeSeconds = estimatedTimeSeconds; }

    public String getEditorialNotes() { return editorialNotes; }
    public void setEditorialNotes(String editorialNotes) { this.editorialNotes = editorialNotes; }

    public LocalDate getLastReviewedAt() { return lastReviewedAt; }
    public void setLastReviewedAt(LocalDate lastReviewedAt) { this.lastReviewedAt = lastReviewedAt; }
}
