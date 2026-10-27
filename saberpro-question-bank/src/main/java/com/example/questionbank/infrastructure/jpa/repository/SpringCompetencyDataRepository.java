package com.example.questionbank.infrastructure.jpa.repository;

import com.example.questionbank.infrastructure.jpa.record.CompetencyRecord;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for CompetencyRecord.
 */
public interface SpringCompetencyDataRepository extends JpaRepository<CompetencyRecord, Long> {
}
