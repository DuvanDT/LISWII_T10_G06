package com.example.questionbank.api.dto;

public class QuestionMetadataDto {
    private String cognitiveLevel;
    private Integer estimatedTimeSeconds;
    private String editorialNotes;

    public QuestionMetadataDto() {}

    public QuestionMetadataDto(String cognitiveLevel, Integer estimatedTimeSeconds, String editorialNotes) {
        this.cognitiveLevel = cognitiveLevel;
        this.estimatedTimeSeconds = estimatedTimeSeconds;
        this.editorialNotes = editorialNotes;
    }

    public String getCognitiveLevel() { return cognitiveLevel; }
    public void setCognitiveLevel(String cognitiveLevel) { this.cognitiveLevel = cognitiveLevel; }

    public Integer getEstimatedTimeSeconds() { return estimatedTimeSeconds; }
    public void setEstimatedTimeSeconds(Integer estimatedTimeSeconds) { this.estimatedTimeSeconds = estimatedTimeSeconds; }

    public String getEditorialNotes() { return editorialNotes; }
    public void setEditorialNotes(String editorialNotes) { this.editorialNotes = editorialNotes; }
}
