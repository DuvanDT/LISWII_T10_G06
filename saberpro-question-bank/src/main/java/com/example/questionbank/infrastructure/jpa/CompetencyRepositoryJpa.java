package com.example.questionbank.infrastructure.jpa;

import com.example.questionbank.domain.model.Competency;
import com.example.questionbank.domain.repository.CompetencyRepository;
import com.example.questionbank.infrastructure.jpa.mapper.QuestionDomainMapper;
import com.example.questionbank.infrastructure.jpa.record.CompetencyRecord;
import com.example.questionbank.infrastructure.jpa.repository.SpringCompetencyDataRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Concrete JPA adapter implementation for CompetencyRepository.
 */
@Repository
public class CompetencyRepositoryJpa implements CompetencyRepository {

    private final SpringCompetencyDataRepository data;

    public CompetencyRepositoryJpa(SpringCompetencyDataRepository data) {
        this.data = data;
    }

    @Override
    public Competency save(Competency competency) {
        CompetencyRecord record = QuestionDomainMapper.toRecord(competency);
        if (competency.getId() != null) {
            record.setId(competency.getId());
        }
        return QuestionDomainMapper.toDomain(data.save(record));
    }

    @Override
    public Optional<Competency> findById(Long id) {
        return data.findById(id).map(QuestionDomainMapper::toDomain);
    }

    @Override
    public List<Competency> findAll() {
        return data.findAll().stream().map(QuestionDomainMapper::toDomain).toList();
    }
}
