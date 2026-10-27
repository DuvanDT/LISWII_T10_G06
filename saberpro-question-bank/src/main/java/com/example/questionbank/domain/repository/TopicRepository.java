package com.example.questionbank.domain.repository;

import com.example.questionbank.domain.model.Topic;

import java.util.List;
import java.util.Optional;

/**
 * Technology-independent repository contract for Topic entities.
 */
public interface TopicRepository {

    Topic save(Topic topic);

    Optional<Topic> findById(Long id);

    List<Topic> findAll();
}
