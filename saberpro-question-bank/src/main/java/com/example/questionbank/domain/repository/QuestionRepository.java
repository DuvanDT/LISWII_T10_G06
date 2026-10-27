package com.example.questionbank.domain.repository;

import com.example.questionbank.domain.model.Question;
import com.example.questionbank.domain.model.QuestionStatus;

import java.util.List;
import java.util.Optional;

/**
 * Technology-independent repository contract for managing Question entities.
 */
public interface QuestionRepository {

    /**
     * Persists or updates a question.
     *
     * @param question domain question entity
     * @return persisted domain question with generated ID
     */
    Question save(Question question);

    /**
     * Finds a question by its unique identifier.
     *
     * @param id question identifier
     * @return optional containing the domain question if found
     */
    Optional<Question> findById(Long id);

    /**
     * Retrieves all questions.
     *
     * @return list of questions
     */
    List<Question> findAll();

    /**
     * Retrieves questions created by a specific author.
     *
     * @param authorId author user identifier
     * @return list of questions
     */
    List<Question> findByAuthorId(Long authorId);

    /**
     * Retrieves questions by lifecycle status.
     *
     * @param status question status
     * @return list of questions
     */
    List<Question> findByStatus(QuestionStatus status);

    /**
     * Retrieves published questions associated with a given competency.
     *
     * @param competencyId competency identifier
     * @return list of published questions
     */
    List<Question> findPublishedByCompetencyId(Long competencyId);

    /**
     * Deletes a question by its identifier.
     *
     * @param id question identifier
     */
    void deleteById(Long id);
}
