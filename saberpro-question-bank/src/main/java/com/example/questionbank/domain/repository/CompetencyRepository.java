package com.example.questionbank.domain.repository;

import com.example.questionbank.domain.model.Competency;

import java.util.List;
import java.util.Optional;

/**
 * Technology-independent repository contract for Competency entities.
 */
public interface CompetencyRepository {

    Competency save(Competency competency);

    Optional<Competency> findById(Long id);

    List<Competency> findAll();
}
