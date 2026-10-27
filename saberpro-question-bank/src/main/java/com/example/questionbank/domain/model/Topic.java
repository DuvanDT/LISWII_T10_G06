package com.example.questionbank.domain.model;

/**
 * Pure domain representation of an academic topic (subject area).
 */
public class Topic {
    private Long id;
    private String name;
    private String description;

    public Topic() {}

    /**
     * Creates a new topic.
     *
     * @param id topic identifier
     * @param name unique name of the topic
     * @param description contextual explanation
     */
    public Topic(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
