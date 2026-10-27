package com.example.questionbank.domain.repository;

import com.example.questionbank.domain.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Technology-independent repository contract for User entities.
 */
public interface UserRepository {

    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    List<User> findAll();
}
