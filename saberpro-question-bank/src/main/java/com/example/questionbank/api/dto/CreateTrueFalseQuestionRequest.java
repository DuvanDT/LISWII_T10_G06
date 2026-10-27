package com.example.questionbank.api.dto;

import com.example.questionbank.domain.model.DifficultyLevel;
import java.util.List;

public class CreateTrueFalseQuestionRequest {
    private String statement;
    private DifficultyLevel difficulty;
    private Long authorId;
    private Long topicId;
    private Boolean expectedValue;
    private String justification;
    private QuestionMetadataDto metadata;
    private List<Long> competencyIds;

    public CreateTrueFalseQuestionRequest() {}

    public String getStatement() { return statement; }
    public void setStatement(String statement) { this.statement = statement; }

    public DifficultyLevel getDifficulty() { return difficulty; }
    public void setDifficulty(DifficultyLevel difficulty) { this.difficulty = difficulty; }

    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }

    public Long getTopicId() { return topicId; }
    public void setTopicId(Long topicId) { this.topicId = topicId; }

    public Boolean getExpectedValue() { return expectedValue; }
    public void setExpectedValue(Boolean expectedValue) { this.expectedValue = expectedValue; }

    public String getJustification() { return justification; }
    public void setJustification(String justification) { this.justification = justification; }

    public QuestionMetadataDto getMetadata() { return metadata; }
    public void setMetadata(QuestionMetadataDto metadata) { this.metadata = metadata; }

    public List<Long> getCompetencyIds() { return competencyIds; }
    public void setCompetencyIds(List<Long> competencyIds) { this.competencyIds = competencyIds; }
}
