package com.example.questionbank.infrastructure.jpa.record;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * JPA entity record for quizzes and simulation evaluations.
 */
@Entity
@Table(name = "quizzes")
public class QuizRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creator_id", nullable = false)
    private UserRecord creator;

    @ManyToMany
    @JoinTable(
        name = "quiz_questions",
        joinColumns = @JoinColumn(name = "quiz_id"),
        inverseJoinColumns = @JoinColumn(name = "question_id")
    )
    private Set<QuestionRecord> questions = new HashSet<>();

    protected QuizRecord() {}

    public QuizRecord(String title, String description, UserRecord creator) {
        this.title = title;
        this.description = description;
        this.creator = creator;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public UserRecord getCreator() { return creator; }
    public void setCreator(UserRecord creator) { this.creator = creator; }

    public Set<QuestionRecord> getQuestions() { return questions; }
    public void setQuestions(Set<QuestionRecord> questions) { this.questions = questions; }

    public void addQuestion(QuestionRecord question) {
        this.questions.add(question);
    }
}
