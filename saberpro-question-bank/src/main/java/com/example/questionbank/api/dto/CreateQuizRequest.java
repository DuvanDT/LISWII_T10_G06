package com.example.questionbank.api.dto;

import java.util.List;

public class CreateQuizRequest {
    private String title;
    private String description;
    private Long creatorId;
    private List<Long> questionIds;

    public CreateQuizRequest() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getCreatorId() { return creatorId; }
    public void setCreatorId(Long creatorId) { this.creatorId = creatorId; }

    public List<Long> getQuestionIds() { return questionIds; }
    public void setQuestionIds(List<Long> questionIds) { this.questionIds = questionIds; }
}
