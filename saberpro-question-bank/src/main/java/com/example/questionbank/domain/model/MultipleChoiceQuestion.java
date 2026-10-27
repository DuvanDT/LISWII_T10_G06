package com.example.questionbank.domain.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Multiple-choice question specialization with answer options.
 */
public class MultipleChoiceQuestion extends Question {
    private final List<AnswerOption> options = new ArrayList<>();

    public MultipleChoiceQuestion() {
        super();
    }

    public MultipleChoiceQuestion(Long id, String statement, DifficultyLevel difficulty,
                                  QuestionStatus status, Long authorId, Long topicId) {
        super(id, statement, difficulty, status, authorId, topicId);
    }

    public List<AnswerOption> getOptions() {
        return List.copyOf(options);
    }

    /**
     * Adds an answer option.
     *
     * @param option answer option to add
     */
    public void addOption(AnswerOption option) {
        if (option != null) {
            this.options.add(option);
        }
    }

    /**
     * Replaces the options list.
     *
     * @param options new options
     */
    public void setOptions(List<AnswerOption> options) {
        this.options.clear();
        if (options != null) {
            this.options.addAll(options);
        }
    }

    /**
     * Validates that options conform to standard Saber Pro single-selection rules:
     * exactly 1 correct answer and at least 2 options (recommended 5 options: 4 distractors + 1 key).
     */
    @Override
    public void validateForReview() {
        if (options.isEmpty()) {
            throw new IllegalStateException("Multiple-choice question must have answer options defined.");
        }
        long correctCount = options.stream().filter(AnswerOption::isCorrect).count();
        if (correctCount != 1) {
            throw new IllegalStateException("Multiple-choice question must have exactly ONE correct option. Found: " + correctCount);
        }
    }
}
