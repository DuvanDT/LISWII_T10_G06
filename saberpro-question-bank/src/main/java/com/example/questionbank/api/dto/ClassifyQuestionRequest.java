package com.example.questionbank.api.dto;

import java.util.List;

public class ClassifyQuestionRequest {
    private Long topicId;
    private List<Long> competencyIds;

    public ClassifyQuestionRequest() {}

    public Long getTopicId() { return topicId; }
    public void setTopicId(Long topicId) { this.topicId = topicId; }

    public List<Long> getCompetencyIds() { return competencyIds; }
    public void setCompetencyIds(List<Long> competencyIds) { this.competencyIds = competencyIds; }
}
