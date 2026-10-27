package com.example.questionbank.infrastructure.jpa;

import com.example.questionbank.domain.model.User;
import com.example.questionbank.domain.repository.UserRepository;
import com.example.questionbank.infrastructure.jpa.mapper.QuestionDomainMapper;
import com.example.questionbank.infrastructure.jpa.record.UserRecord;
import com.example.questionbank.infrastructure.jpa.repository.SpringUserDataRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Concrete JPA adapter implementation for UserRepository.
 */
@Repository
public class UserRepositoryJpa implements UserRepository {

    private final SpringUserDataRepository data;

    public UserRepositoryJpa(SpringUserDataRepository data) {
        this.data = data;
    }

    @Override
    public User save(User user) {
        UserRecord record = QuestionDomainMapper.toRecord(user);
        if (user.getId() != null) {
            record.setId(user.getId());
        }
        return QuestionDomainMapper.toDomain(data.save(record));
    }

    @Override
    public Optional<User> findById(Long id) {
        return data.findById(id).map(QuestionDomainMapper::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return data.findByUsername(username).map(QuestionDomainMapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return data.findAll().stream().map(QuestionDomainMapper::toDomain).toList();
    }
}
