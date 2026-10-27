package com.example.questionbank.infrastructure.jpa.repository;

import com.example.questionbank.domain.model.QuestionStatus;
import com.example.questionbank.infrastructure.jpa.record.QuestionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for QuestionRecord hierarchy.
 */
public interface SpringQuestionDataRepository extends JpaRepository<QuestionRecord, Long> {

    List<QuestionRecord> findByAuthorId(Long authorId);

    List<QuestionRecord> findByStatus(QuestionStatus status);

    @Query("SELECT q FROM QuestionRecord q JOIN q.competencies c WHERE q.status = :status AND c.id = :competencyId")
    List<QuestionRecord> findByStatusAndCompetencyId(@Param("status") QuestionStatus status, @Param("competencyId") Long competencyId);
}
