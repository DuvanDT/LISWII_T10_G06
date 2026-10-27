package com.example.questionbank.infrastructure;

import com.example.questionbank.domain.model.*;
import com.example.questionbank.domain.repository.CompetencyRepository;
import com.example.questionbank.domain.repository.TopicRepository;
import com.example.questionbank.domain.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Initializes baseline test data for users, topics, and competencies upon application boot.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository users;
    private final TopicRepository topics;
    private final CompetencyRepository competencies;

    public DataInitializer(UserRepository users, TopicRepository topics, CompetencyRepository competencies) {
        this.users = users;
        this.topics = topics;
        this.competencies = competencies;
    }

    @Override
    public void run(String... args) {
        if (users.findAll().isEmpty()) {
            // Seed Users
            users.save(new User(null, "author_ana", "ana@unicauca.edu.co", "Ana Perez", UserRole.AUTHOR));
            users.save(new User(null, "reviewer_carlos", "carlos@unicauca.edu.co", "Carlos Gomez", UserRole.REVIEWER));
            users.save(new User(null, "teacher_maria", "maria@unicauca.edu.co", "Prof. Maria Lopez", UserRole.TEACHER));
            users.save(new User(null, "admin_wilson", "admin@unicauca.edu.co", "Wilson Admin", UserRole.ADMIN));

            // Seed Topics
            topics.save(new Topic(null, "Software Architecture", "Patterns, architectural styles, and quality attributes"));
            topics.save(new Topic(null, "Database Systems", "Relational persistence, transactions, and normalization"));

            // Seed Competencies
            competencies.save(new Competency(null, "COMP-01", "Design and Modeling", "Ability to design software solutions and persistence models"));
            competencies.save(new Competency(null, "COMP-02", "Critical Analysis", "Capacity to evaluate trade-offs in software architecture"));
        }
    }
}
