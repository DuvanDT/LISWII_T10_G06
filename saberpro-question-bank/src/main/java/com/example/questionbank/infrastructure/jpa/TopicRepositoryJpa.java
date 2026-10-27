package com.example.questionbank.infrastructure.jpa;

import com.example.questionbank.domain.model.Topic;
import com.example.questionbank.domain.repository.TopicRepository;
import com.example.questionbank.infrastructure.jpa.mapper.QuestionDomainMapper;
import com.example.questionbank.infrastructure.jpa.record.TopicRecord;
import com.example.questionbank.infrastructure.jpa.repository.SpringTopicDataRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Concrete JPA adapter implementation for TopicRepository.
 */
@Repository
public class TopicRepositoryJpa implements TopicRepository {

    private final SpringTopicDataRepository data;

    public TopicRepositoryJpa(SpringTopicDataRepository data) {
        this.data = data;
    }

    @Override
    public Topic save(Topic topic) {
        TopicRecord record = QuestionDomainMapper.toRecord(topic);
        if (topic.getId() != null) {
            record.setId(topic.getId());
        }
        return QuestionDomainMapper.toDomain(data.save(record));
    }

    @Override
    public Optional<Topic> findById(Long id) {
        return data.findById(id).map(QuestionDomainMapper::toDomain);
    }

    @Override
    public List<Topic> findAll() {
        return data.findAll().stream().map(QuestionDomainMapper::toDomain).toList();
    }
}
