package com.example.questionbank.domain.model;

/**
 * Lifecycle states of a question in the Saber Pro Question Bank.
 */
public enum QuestionStatus {
    /** Initial authoring state. Free editing allowed. */
    DRAFT,
    /** Submitted for review. Awaiting assignment to a reviewer. */
    PENDING_REVIEW,
    /** Under active pedagogical and technical evaluation. */
    IN_REVIEW,
    /** Approved by reviewer. Ready for publication. */
    APPROVED,
    /** Published and eligible for inclusion in simulation quizzes. */
    PUBLISHED,
    /** Retired or obsolete question. */
    ARCHIVED
}
