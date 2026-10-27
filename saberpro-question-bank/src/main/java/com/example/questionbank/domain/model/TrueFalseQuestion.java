package com.example.questionbank.domain.model;

/**
 * True/False question specialization with an expected boolean truth value.
 */
public class TrueFalseQuestion extends Question {
    private Boolean expectedValue;
    private String justification;

    public TrueFalseQuestion() {
        super();
    }

    public TrueFalseQuestion(Long id, String statement, DifficultyLevel difficulty,
                             QuestionStatus status, Long authorId, Long topicId,
                             Boolean expectedValue, String justification) {
        super(id, statement, difficulty, status, authorId, topicId);
        this.expectedValue = expectedValue;
        this.justification = justification;
    }

    public Boolean getExpectedValue() { return expectedValue; }
    public void setExpectedValue(Boolean expectedValue) { this.expectedValue = expectedValue; }

    public String getJustification() { return justification; }
    public void setJustification(String justification) { this.justification = justification; }

    @Override
    public void validateForReview() {
        if (expectedValue == null) {
            throw new IllegalStateException("True/False question must define an expected boolean value (true or false).");
        }
    }
}
