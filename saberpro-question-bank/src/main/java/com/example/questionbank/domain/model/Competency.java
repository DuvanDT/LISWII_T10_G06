package com.example.questionbank.domain.model;

/**
 * Pure domain representation of an academic competency assessed by Saber Pro.
 */
public class Competency {
    private Long id;
    private String code;
    private String name;
    private String description;

    public Competency() {}

    /**
     * Creates a new competency.
     *
     * @param id competency identifier
     * @param code official code (e.g., "COMP-SW-01")
     * @param name human-readable title
     * @param description skills and knowledge evaluated
     */
    public Competency(Long id, String code, String name, String description) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
