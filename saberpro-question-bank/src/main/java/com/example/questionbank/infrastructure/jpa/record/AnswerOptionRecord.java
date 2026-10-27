package com.example.questionbank.infrastructure.jpa.record;

import jakarta.persistence.*;

/**
 * JPA entity record for multiple-choice answer options (Many-to-One owning side).
 */
@Entity
@Table(name = "answer_options")
public class AnswerOptionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private MultipleChoiceQuestionRecord question;

    @Column(nullable = false, length = 500)
    private String text;

    @Column(length = 1000)
    private String explanation;

    @Column(name = "is_correct", nullable = false)
    private boolean correct;

    protected AnswerOptionRecord() {}

    public AnswerOptionRecord(String text, String explanation, boolean correct) {
        this.text = text;
        this.explanation = explanation;
        this.correct = correct;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public MultipleChoiceQuestionRecord getQuestion() { return question; }
    public void setQuestion(MultipleChoiceQuestionRecord question) { this.question = question; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public boolean isCorrect() { return correct; }
    public void setCorrect(boolean correct) { this.correct = correct; }
}
