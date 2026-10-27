package com.example.questionbank.infrastructure.jpa.record;

import com.example.questionbank.domain.model.DifficultyLevel;
import com.example.questionbank.domain.model.QuestionStatus;
import jakarta.persistence.*;

/**
 * JPA entity record for true/false questions (subclass in JOINED hierarchy).
 */
@Entity
@Table(name = "true_false_questions")
public class TrueFalseQuestionRecord extends QuestionRecord {

    @Column(name = "expected_value", nullable = false)
    private boolean expectedValue;

    @Column(length = 500)
    private String justification;

    protected TrueFalseQuestionRecord() {
        super();
    }

    public TrueFalseQuestionRecord(String statement, DifficultyLevel difficulty,
                                   QuestionStatus status, UserRecord author, TopicRecord topic,
                                   boolean expectedValue, String justification) {
        super(statement, difficulty, status, author, topic);
        this.expectedValue = expectedValue;
        this.justification = justification;
    }

    public boolean isExpectedValue() { return expectedValue; }
    public void setExpectedValue(boolean expectedValue) { this.expectedValue = expectedValue; }

    public String getJustification() { return justification; }
    public void setJustification(String justification) { this.justification = justification; }
}
