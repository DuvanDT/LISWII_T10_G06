package com.example.questionbank.infrastructure.jpa;

import com.example.questionbank.domain.model.Question;
import com.example.questionbank.domain.model.QuestionStatus;
import com.example.questionbank.domain.repository.QuestionRepository;
import com.example.questionbank.infrastructure.jpa.mapper.QuestionDomainMapper;
import com.example.questionbank.infrastructure.jpa.record.CompetencyRecord;
import com.example.questionbank.infrastructure.jpa.record.QuestionRecord;
import com.example.questionbank.infrastructure.jpa.record.TopicRecord;
import com.example.questionbank.infrastructure.jpa.record.UserRecord;
import com.example.questionbank.infrastructure.jpa.repository.SpringCompetencyDataRepository;
import com.example.questionbank.infrastructure.jpa.repository.SpringQuestionDataRepository;
import com.example.questionbank.infrastructure.jpa.repository.SpringTopicDataRepository;
import com.example.questionbank.infrastructure.jpa.repository.SpringUserDataRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Concrete JPA adapter implementation for QuestionRepository.
 * Handles database joins, cascading, and mapping between domain POJOs and JPA records.
 */
@Repository
public class QuestionRepositoryJpa implements QuestionRepository {

    private final SpringQuestionDataRepository questionData;
    private final SpringUserDataRepository userData;
    private final SpringTopicDataRepository topicData;
    private final SpringCompetencyDataRepository competencyData;

    public QuestionRepositoryJpa(SpringQuestionDataRepository questionData,
                                 SpringUserDataRepository userData,
                                 SpringTopicDataRepository topicData,
                                 SpringCompetencyDataRepository competencyData) {
        this.questionData = questionData;
        this.userData = userData;
        this.topicData = topicData;
        this.competencyData = competencyData;
    }

    @Override
    @Transactional
    public Question save(Question question) {
        UserRecord author = userData.findById(question.getAuthorId())
            .orElseThrow(() -> new IllegalArgumentException("Author not found with ID: " + question.getAuthorId()));

        TopicRecord topic = topicData.findById(question.getTopicId())
            .orElseThrow(() -> new IllegalArgumentException("Topic not found with ID: " + question.getTopicId()));

        Set<CompetencyRecord> competencies = new HashSet<>();
        if (question.getCompetencyIds() != null && !question.getCompetencyIds().isEmpty()) {
            competencies.addAll(competencyData.findAllById(question.getCompetencyIds()));
        }

        QuestionRecord record = QuestionDomainMapper.toRecord(question, author, topic, competencies);
        QuestionRecord saved = questionData.save(record);
        return QuestionDomainMapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Question> findById(Long id) {
        return questionData.findById(id).map(QuestionDomainMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Question> findAll() {
        return questionData.findAll().stream().map(QuestionDomainMapper::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Question> findByAuthorId(Long authorId) {
        return questionData.findByAuthorId(authorId).stream().map(QuestionDomainMapper::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Question> findByStatus(QuestionStatus status) {
        return questionData.findByStatus(status).stream().map(QuestionDomainMapper::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Question> findPublishedByCompetencyId(Long competencyId) {
        return questionData.findByStatusAndCompetencyId(QuestionStatus.PUBLISHED, competencyId)
            .stream().map(QuestionDomainMapper::toDomain).toList();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        questionData.deleteById(id);
    }
}
