package com.example.questionbank.domain.model;

/**
 * Roles for users interacting with the Saber Pro Question Bank.
 */
public enum UserRole {
    /** Creates and modifies questions. */
    AUTHOR,
    /** Evaluates quality and pertinence of questions. */
    REVIEWER,
    /** Organizes quizzes and simulation tests. */
    TEACHER,
    /** Answers questions and reviews results. */
    STUDENT,
    /** Manages system parameters, users, and publication. */
    ADMIN
}
