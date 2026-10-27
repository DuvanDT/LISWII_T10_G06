package com.example.questionbank.infrastructure.jpa.record;

import com.example.questionbank.domain.model.DifficultyLevel;
import com.example.questionbank.domain.model.QuestionStatus;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Root abstract JPA entity for Question hierarchy using JOINED table inheritance.
 */
@Entity
@Table(name = "questions")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class QuestionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String statement;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DifficultyLevel difficulty;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private QuestionStatus status = QuestionStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private UserRecord author;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "topic_id", nullable = false)
    private TopicRecord topic;

    @OneToOne(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private QuestionMetadataRecord metadata;

    @ManyToMany
    @JoinTable(
        name = "question_competencies",
        joinColumns = @JoinColumn(name = "question_id"),
        inverseJoinColumns = @JoinColumn(name = "competency_id")
    )
    private Set<CompetencyRecord> competencies = new HashSet<>();

    protected QuestionRecord() {}

    public QuestionRecord(String statement, DifficultyLevel difficulty, QuestionStatus status,
                          UserRecord author, TopicRecord topic) {
        this.statement = statement;
        this.difficulty = difficulty;
        this.status = status != null ? status : QuestionStatus.DRAFT;
        this.author = author;
        this.topic = topic;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStatement() { return statement; }
    public void setStatement(String statement) { this.statement = statement; }

    public DifficultyLevel getDifficulty() { return difficulty; }
    public void setDifficulty(DifficultyLevel difficulty) { this.difficulty = difficulty; }

    public QuestionStatus getStatus() { return status; }
    public void setStatus(QuestionStatus status) { this.status = status; }

    public UserRecord getAuthor() { return author; }
    public void setAuthor(UserRecord author) { this.author = author; }

    public TopicRecord getTopic() { return topic; }
    public void setTopic(TopicRecord topic) { this.topic = topic; }

    public QuestionMetadataRecord getMetadata() { return metadata; }
    public void setMetadata(QuestionMetadataRecord metadata) {
        this.metadata = metadata;
        if (metadata != null) {
            metadata.setQuestion(this);
        }
    }

    public Set<CompetencyRecord> getCompetencies() { return competencies; }
    public void setCompetencies(Set<CompetencyRecord> competencies) { this.competencies = competencies; }

    public void addCompetency(CompetencyRecord competency) {
        this.competencies.add(competency);
    }
}
