package com.example.questionbank.api.dto;

public class ReviewFeedbackRequest {
    private String feedback;

    public ReviewFeedbackRequest() {}
    public ReviewFeedbackRequest(String feedback) { this.feedback = feedback; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
}
