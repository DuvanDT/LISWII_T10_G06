package com.example.questionbank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application entry point for the Saber Pro Question Bank system.
 * Follows a clean layered persistence architecture.
 */
@SpringBootApplication
public class QuestionBankApplication {

    /**
     * Application entry point.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(QuestionBankApplication.class, args);
    }
}
