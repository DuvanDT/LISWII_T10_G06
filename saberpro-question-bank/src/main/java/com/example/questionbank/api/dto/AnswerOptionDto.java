package com.example.questionbank.api.dto;

public class AnswerOptionDto {
    private String text;
    private String explanation;
    private boolean correct;

    public AnswerOptionDto() {}

    public AnswerOptionDto(String text, String explanation, boolean correct) {
        this.text = text;
        this.explanation = explanation;
        this.correct = correct;
    }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public boolean isCorrect() { return correct; }
    public void setCorrect(boolean correct) { this.correct = correct; }
}
