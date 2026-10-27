package com.example.questionbank.infrastructure.jpa.record;

import com.example.questionbank.domain.model.DifficultyLevel;
import com.example.questionbank.domain.model.QuestionStatus;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA entity record for multiple-choice questions (subclass in JOINED hierarchy).
 */
@Entity
@Table(name = "multiple_choice_questions")
public class MultipleChoiceQuestionRecord extends QuestionRecord {

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AnswerOptionRecord> options = new ArrayList<>();

    protected MultipleChoiceQuestionRecord() {
        super();
    }

    public MultipleChoiceQuestionRecord(String statement, DifficultyLevel difficulty,
                                        QuestionStatus status, UserRecord author, TopicRecord topic) {
        super(statement, difficulty, status, author, topic);
    }

    public List<AnswerOptionRecord> getOptions() {
        return options;
    }

    public void setOptions(List<AnswerOptionRecord> options) {
        this.options.clear();
        if (options != null) {
            for (AnswerOptionRecord opt : options) {
                addOption(opt);
            }
        }
    }

    public void addOption(AnswerOptionRecord option) {
        this.options.add(option);
        option.setQuestion(this);
    }
}
