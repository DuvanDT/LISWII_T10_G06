package com.example.questionbank.domain.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Pure domain representation of an evaluation quiz or simulation test.
 */
public class Quiz {
    private Long id;
    private String title;
    private String description;
    private Long creatorId;
    private final List<Long> questionIds = new ArrayList<>();

    public Quiz() {}

    /**
     * Creates a new quiz.
     *
     * @param id quiz identifier
     * @param title quiz title
     * @param description test instructions or description
     * @param creatorId teacher or admin creator identifier
     */
    public Quiz(Long id, String title, String description, Long creatorId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.creatorId = creatorId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getCreatorId() { return creatorId; }
    public void setCreatorId(Long creatorId) { this.creatorId = creatorId; }

    public List<Long> getQuestionIds() { return List.copyOf(questionIds); }

    /**
     * Adds a question identifier to this quiz.
     *
     * @param questionId identifier of question to include
     */
    public void addQuestionId(Long questionId) {
        if (questionId != null && !questionIds.contains(questionId)) {
            questionIds.add(questionId);
        }
    }

    /**
     * Replaces question identifiers.
     *
     * @param ids collection of question IDs
     */
    public void setQuestionIds(List<Long> ids) {
        this.questionIds.clear();
        if (ids != null) {
            ids.forEach(this::addQuestionId);
        }
    }
}
