package com.example.questionbank.infrastructure.jpa;

import com.example.questionbank.domain.model.Quiz;
import com.example.questionbank.domain.repository.QuizRepository;
import com.example.questionbank.infrastructure.jpa.mapper.QuestionDomainMapper;
import com.example.questionbank.infrastructure.jpa.record.QuestionRecord;
import com.example.questionbank.infrastructure.jpa.record.QuizRecord;
import com.example.questionbank.infrastructure.jpa.record.UserRecord;
import com.example.questionbank.infrastructure.jpa.repository.SpringQuestionDataRepository;
import com.example.questionbank.infrastructure.jpa.repository.SpringQuizDataRepository;
import com.example.questionbank.infrastructure.jpa.repository.SpringUserDataRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Concrete JPA adapter implementation for QuizRepository.
 */
@Repository
public class QuizRepositoryJpa implements QuizRepository {

    private final SpringQuizDataRepository quizData;
    private final SpringUserDataRepository userData;
    private final SpringQuestionDataRepository questionData;

    public QuizRepositoryJpa(SpringQuizDataRepository quizData,
                             SpringUserDataRepository userData,
                             SpringQuestionDataRepository questionData) {
        this.quizData = quizData;
        this.userData = userData;
        this.questionData = questionData;
    }

    @Override
    @Transactional
    public Quiz save(Quiz quiz) {
        UserRecord creator = userData.findById(quiz.getCreatorId())
            .orElseThrow(() -> new IllegalArgumentException("Creator user not found with ID: " + quiz.getCreatorId()));

        QuizRecord record = new QuizRecord(quiz.getTitle(), quiz.getDescription(), creator);
        if (quiz.getId() != null) {
            record.setId(quiz.getId());
        }

        if (quiz.getQuestionIds() != null && !quiz.getQuestionIds().isEmpty()) {
            Set<QuestionRecord> questions = new HashSet<>(questionData.findAllById(quiz.getQuestionIds()));
            record.setQuestions(questions);
        }

        QuizRecord saved = quizData.save(record);
        return QuestionDomainMapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Quiz> findById(Long id) {
        return quizData.findById(id).map(QuestionDomainMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Quiz> findAll() {
        return quizData.findAll().stream().map(QuestionDomainMapper::toDomain).toList();
    }
}
