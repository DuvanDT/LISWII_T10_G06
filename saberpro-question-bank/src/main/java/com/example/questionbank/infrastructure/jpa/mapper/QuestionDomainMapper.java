package com.example.questionbank.infrastructure.jpa.mapper;

import com.example.questionbank.domain.model.*;
import com.example.questionbank.infrastructure.jpa.record.*;

import java.util.List;
import java.util.Set;

/**
 * Maps between pure domain entities and JPA persistence records.
 * Keeps domain free of persistence logic.
 */
public final class QuestionDomainMapper {

    private QuestionDomainMapper() {}

    // --- User Mapping ---
    public static User toDomain(UserRecord record) {
        if (record == null) return null;
        return new User(record.getId(), record.getUsername(), record.getEmail(),
                        record.getFullName(), record.getRole());
    }

    public static UserRecord toRecord(User domain) {
        if (domain == null) return null;
        return new UserRecord(domain.getUsername(), domain.getEmail(),
                              domain.getFullName(), domain.getRole());
    }

    // --- Topic Mapping ---
    public static Topic toDomain(TopicRecord record) {
        if (record == null) return null;
        return new Topic(record.getId(), record.getName(), record.getDescription());
    }

    public static TopicRecord toRecord(Topic domain) {
        if (domain == null) return null;
        return new TopicRecord(domain.getName(), domain.getDescription());
    }

    // --- Competency Mapping ---
    public static Competency toDomain(CompetencyRecord record) {
        if (record == null) return null;
        return new Competency(record.getId(), record.getCode(), record.getName(), record.getDescription());
    }

    public static CompetencyRecord toRecord(Competency domain) {
        if (domain == null) return null;
        return new CompetencyRecord(domain.getCode(), domain.getName(), domain.getDescription());
    }

    // --- Metadata Mapping ---
    public static QuestionMetadata toDomain(QuestionMetadataRecord record) {
        if (record == null) return null;
        return new QuestionMetadata(record.getId(), record.getCognitiveLevel(),
                                    record.getEstimatedTimeSeconds(), record.getEditorialNotes(),
                                    record.getLastReviewedAt());
    }

    public static QuestionMetadataRecord toRecord(QuestionMetadata domain) {
        if (domain == null) return null;
        return new QuestionMetadataRecord(domain.getCognitiveLevel(), domain.getEstimatedTimeSeconds(),
                                          domain.getEditorialNotes(), domain.getLastReviewedAt());
    }

    // --- AnswerOption Mapping ---
    public static AnswerOption toDomain(AnswerOptionRecord record) {
        if (record == null) return null;
        return new AnswerOption(record.getId(), record.getText(), record.getExplanation(), record.isCorrect());
    }

    public static AnswerOptionRecord toRecord(AnswerOption domain) {
        if (domain == null) return null;
        return new AnswerOptionRecord(domain.getText(), domain.getExplanation(), domain.isCorrect());
    }

    // --- Question Polymorphic Mapping ---
    public static Question toDomain(QuestionRecord record) {
        if (record == null) return null;

        Question domain;
        if (record instanceof MultipleChoiceQuestionRecord mcRecord) {
            MultipleChoiceQuestion mc = new MultipleChoiceQuestion(
                mcRecord.getId(), mcRecord.getStatement(), mcRecord.getDifficulty(),
                mcRecord.getStatus(), mcRecord.getAuthor().getId(), mcRecord.getTopic().getId()
            );
            if (mcRecord.getOptions() != null) {
                mcRecord.getOptions().forEach(opt -> mc.addOption(toDomain(opt)));
            }
            domain = mc;
        } else if (record instanceof TrueFalseQuestionRecord tfRecord) {
            domain = new TrueFalseQuestion(
                tfRecord.getId(), tfRecord.getStatement(), tfRecord.getDifficulty(),
                tfRecord.getStatus(), tfRecord.getAuthor().getId(), tfRecord.getTopic().getId(),
                tfRecord.isExpectedValue(), tfRecord.getJustification()
            );
        } else {
            throw new IllegalArgumentException("Unknown QuestionRecord type: " + record.getClass().getName());
        }

        // Common associations mapped as IDs or child domain objects
        if (record.getMetadata() != null) {
            domain.setMetadata(toDomain(record.getMetadata()));
        }
        if (record.getCompetencies() != null) {
            record.getCompetencies().forEach(c -> domain.addCompetencyId(c.getId()));
        }

        return domain;
    }

    public static QuestionRecord toRecord(Question domain, UserRecord author, TopicRecord topic,
                                          Set<CompetencyRecord> competencies) {
        if (domain == null) return null;

        QuestionRecord record;
        if (domain instanceof MultipleChoiceQuestion mc) {
            MultipleChoiceQuestionRecord mcRecord = new MultipleChoiceQuestionRecord(
                mc.getStatement(), mc.getDifficulty(), mc.getStatus(), author, topic
            );
            if (mc.getOptions() != null) {
                for (AnswerOption opt : mc.getOptions()) {
                    AnswerOptionRecord optRecord = toRecord(opt);
                    mcRecord.addOption(optRecord);
                }
            }
            record = mcRecord;
        } else if (domain instanceof TrueFalseQuestion tf) {
            record = new TrueFalseQuestionRecord(
                tf.getStatement(), tf.getDifficulty(), tf.getStatus(), author, topic,
                Boolean.TRUE.equals(tf.getExpectedValue()), tf.getJustification()
            );
        } else {
            throw new IllegalArgumentException("Unsupported domain Question type: " + domain.getClass().getName());
        }

        if (domain.getId() != null) {
            record.setId(domain.getId());
        }

        if (domain.getMetadata() != null) {
            QuestionMetadataRecord metaRecord = toRecord(domain.getMetadata());
            if (domain.getMetadata().getId() != null) {
                metaRecord.setId(domain.getMetadata().getId());
            }
            record.setMetadata(metaRecord);
        }

        if (competencies != null) {
            record.setCompetencies(competencies);
        }

        return record;
    }

    // --- Quiz Mapping ---
    public static Quiz toDomain(QuizRecord record) {
        if (record == null) return null;
        Quiz quiz = new Quiz(record.getId(), record.getTitle(), record.getDescription(), record.getCreator().getId());
        if (record.getQuestions() != null) {
            record.getQuestions().forEach(q -> quiz.addQuestionId(q.getId()));
        }
        return quiz;
    }
}
