package com.example.questionbank.domain.model;

/**
 * Pure domain representation of an answer option (distractor or correct choice).
 */
public class AnswerOption {
    private Long id;
    private String text;
    private String explanation;
    private boolean correct;

    public AnswerOption() {}

    /**
     * Creates an answer option.
     *
     * @param id identifier
     * @param text option wording
     * @param explanation pedagogical explanation why it is correct or incorrect
     * @param correct true if this option represents the correct answer key
     */
    public AnswerOption(Long id, String text, String explanation, boolean correct) {
        this.id = id;
        this.text = text;
        this.explanation = explanation;
        this.correct = correct;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public boolean isCorrect() { return correct; }
    public void setCorrect(boolean correct) { this.correct = correct; }
}
