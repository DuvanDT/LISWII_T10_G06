package com.example.questionbank;

import com.example.questionbank.application.QuestionService;
import com.example.questionbank.application.QuizService;
import com.example.questionbank.domain.model.*;
import com.example.questionbank.domain.repository.CompetencyRepository;
import com.example.questionbank.domain.repository.QuestionRepository;
import com.example.questionbank.domain.repository.QuizRepository;
import com.example.questionbank.domain.repository.TopicRepository;
import com.example.questionbank.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for Spring Data JPA and H2 database persistence.
 * Verifies relational mappings, JOINED inheritance, cascading, and business rule enforcement.
 */
@SpringBootTest
@Transactional
class QuestionBankIntegrationTest {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuizService quizService;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private CompetencyRepository competencyRepository;

    @Autowired
    private QuizRepository quizRepository;

    private Long authorId;
    private Long teacherId;
    private Long topicId;
    private Long competencyId1;
    private Long competencyId2;

    @BeforeEach
    void setupFixtures() {
        User author = userRepository.save(new User(null, "author_test", "author@test.com", "Test Author", UserRole.AUTHOR));
        authorId = author.getId();

        User teacher = userRepository.save(new User(null, "teacher_test", "teacher@test.com", "Test Teacher", UserRole.TEACHER));
        teacherId = teacher.getId();

        Topic topic = topicRepository.save(new Topic(null, "Software Engineering", "SE principles and practices"));
        topicId = topic.getId();

        Competency c1 = competencyRepository.save(new Competency(null, "COMP-TST-01", "Testing", "Writing integration tests"));
        competencyId1 = c1.getId();

        Competency c2 = competencyRepository.save(new Competency(null, "COMP-TST-02", "Architecture", "Layered software design"));
        competencyId2 = c2.getId();
    }

    @Test
    @DisplayName("Punto 9.1 & 9.2: Should save and retrieve MultipleChoiceQuestion preserving 1-1, 1-N, N-1, and N-M relations")
    void testSaveAndRetrieveMultipleChoiceWithAllRelationships() {
        List<AnswerOption> options = List.of(
            new AnswerOption(null, "Option A", "Distractor 1", false),
            new AnswerOption(null, "Option B", "Distractor 2", false),
            new AnswerOption(null, "Option C", "Correct Answer Key", true),
            new AnswerOption(null, "Option D", "Distractor 3", false),
            new AnswerOption(null, "Option E", "Distractor 4", false)
        );

        QuestionMetadata metadata = new QuestionMetadata(
            null, "Application", 90, "Approved by testing committee", LocalDate.now()
        );

        MultipleChoiceQuestion created = questionService.createMultipleChoiceQuestion(
            "Which architectural pattern isolates business rules from technological dependencies?",
            DifficultyLevel.MEDIUM,
            authorId,
            topicId,
            options,
            metadata,
            List.of(competencyId1, competencyId2)
        );

        assertNotNull(created.getId(), "Generated ID must be assigned by H2 database");

        // Retrieve and assert
        Question retrieved = questionService.getQuestionById(created.getId());
        assertInstanceOf(MultipleChoiceQuestion.class, retrieved);

        MultipleChoiceQuestion mcRetrieved = (MultipleChoiceQuestion) retrieved;
        assertEquals(5, mcRetrieved.getOptions().size(), "1-to-N: All 5 answer options must be preserved");
        assertTrue(mcRetrieved.getOptions().stream().anyMatch(AnswerOption::isCorrect), "Answer key preserved");

        assertNotNull(mcRetrieved.getMetadata(), "1-to-1: QuestionMetadata must be present");
        assertEquals("Application", mcRetrieved.getMetadata().getCognitiveLevel());
        assertEquals(90, mcRetrieved.getMetadata().getEstimatedTimeSeconds());

        assertEquals(authorId, mcRetrieved.getAuthorId(), "Many-to-1: Author ID preserved");
        assertEquals(topicId, mcRetrieved.getTopicId(), "Many-to-1: Topic ID preserved");

        assertEquals(2, mcRetrieved.getCompetencyIds().size(), "Many-to-Many: Both competencies preserved");
        assertTrue(mcRetrieved.getCompetencyIds().contains(competencyId1));
        assertTrue(mcRetrieved.getCompetencyIds().contains(competencyId2));
    }

    @Test
    @DisplayName("Punto 9.3: Inheritance JOINED strategy correctly differentiates MultipleChoice and TrueFalse subtypes")
    void testJoinedInheritancePolymorphism() {
        // Create 1 MultipleChoice Question
        MultipleChoiceQuestion mc = questionService.createMultipleChoiceQuestion(
            "What does DDL stand for?",
            DifficultyLevel.EASY,
            authorId,
            topicId,
            List.of(
                new AnswerOption(null, "Data Definition Language", "Correct", true),
                new AnswerOption(null, "Direct Data Layer", "Wrong", false)
            ),
            new QuestionMetadata(null, "Recall", 30, "Basic", LocalDate.now()),
            List.of(competencyId1)
        );

        // Create 1 TrueFalse Question
        TrueFalseQuestion tf = questionService.createTrueFalseQuestion(
            "In JPA JOINED inheritance, each subclass gets its own specialized database table.",
            DifficultyLevel.MEDIUM,
            authorId,
            topicId,
            true,
            "JOINED creates a base table and one table per subclass with a foreign key PK.",
            new QuestionMetadata(null, "Comprehension", 45, "JPA theory", LocalDate.now()),
            List.of(competencyId2)
        );

        // Retrieve all questions polymorphically
        List<Question> all = questionRepository.findAll();
        assertTrue(all.size() >= 2);

        Question retrievedMc = questionRepository.findById(mc.getId()).orElseThrow();
        Question retrievedTf = questionRepository.findById(tf.getId()).orElseThrow();

        assertInstanceOf(MultipleChoiceQuestion.class, retrievedMc, "Should load as MultipleChoiceQuestion subclass");
        assertInstanceOf(TrueFalseQuestion.class, retrievedTf, "Should load as TrueFalseQuestion subclass");

        TrueFalseQuestion tfCast = (TrueFalseQuestion) retrievedTf;
        assertTrue(tfCast.getExpectedValue());
        assertEquals("JOINED creates a base table and one table per subclass with a foreign key PK.",
                     tfCast.getJustification());
    }

    @Test
    @DisplayName("Punto 9.4: Foreign key integrity prevents creating questions with non-existent author or topic")
    void testReferentialIntegrityPreventsInvalidRelations() {
        Long nonExistentAuthorId = 99999L;
        Long nonExistentTopicId = 88888L;

        assertThrows(IllegalArgumentException.class, () ->
            questionService.createTrueFalseQuestion(
                "Statement", DifficultyLevel.EASY, nonExistentAuthorId, topicId, true, "Desc", null, null
            ), "Should reject non-existent author"
        );

        assertThrows(IllegalArgumentException.class, () ->
            questionService.createTrueFalseQuestion(
                "Statement", DifficultyLevel.EASY, authorId, nonExistentTopicId, true, "Desc", null, null
            ), "Should reject non-existent topic"
        );
    }

    @Test
    @DisplayName("Punto 9.5: Business rule enforcement - Quiz MUST reject non-published questions")
    void testQuizRejectsNonPublishedQuestions() {
        // 1. Create a question in DRAFT
        TrueFalseQuestion draftQuestion = questionService.createTrueFalseQuestion(
            "Clean persistence permits @Entity in domain models.",
            DifficultyLevel.EASY,
            authorId,
            topicId,
            false,
            "Clean persistence strictly separates domain from persistence.",
            new QuestionMetadata(null, "Understanding", 30, "Notes", LocalDate.now()),
            List.of(competencyId1)
        );

        assertEquals(QuestionStatus.DRAFT, draftQuestion.getStatus());

        // 2. Attempt to create Quiz with DRAFT question -> Must fail
        IllegalStateException ex1 = assertThrows(IllegalStateException.class, () ->
            quizService.createQuiz("Midterm Simulation", "Simulacro 1", teacherId, List.of(draftQuestion.getId()))
        );
        assertTrue(ex1.getMessage().contains("Only PUBLISHED questions are allowed"));

        // 3. Move question through lifecycle to APPROVED -> Still not PUBLISHED
        questionService.submitForReview(draftQuestion.getId());
        questionService.startReview(draftQuestion.getId());
        questionService.approveQuestion(draftQuestion.getId());

        IllegalStateException ex2 = assertThrows(IllegalStateException.class, () ->
            quizService.createQuiz("Midterm Simulation", "Simulacro 1", teacherId, List.of(draftQuestion.getId()))
        );
        assertTrue(ex2.getMessage().contains("Only PUBLISHED questions are allowed"));

        // 4. Publish question -> Adding to Quiz MUST succeed!
        questionService.publishQuestion(draftQuestion.getId());
        assertEquals(QuestionStatus.PUBLISHED, questionService.getQuestionById(draftQuestion.getId()).getStatus());

        Quiz createdQuiz = quizService.createQuiz(
            "Saber Pro Final Simulation", "Comprehensive test", teacherId, List.of(draftQuestion.getId())
        );

        assertNotNull(createdQuiz.getId());
        assertEquals(1, createdQuiz.getQuestionIds().size());
        assertTrue(createdQuiz.getQuestionIds().contains(draftQuestion.getId()));
    }

    @Test
    @DisplayName("Punto 9.6: Classification and query of published questions by competency")
    void testQueryPublishedByCompetency() {
        TrueFalseQuestion q = questionService.createTrueFalseQuestion(
            "H2 is an in-memory relational database.",
            DifficultyLevel.EASY,
            authorId,
            topicId,
            true,
            "H2 runs embedded in JVM memory.",
            new QuestionMetadata(null, "Recall", 20, "H2 info", LocalDate.now()),
            List.of(competencyId1)
        );

        // Advance to PUBLISHED
        questionService.submitForReview(q.getId());
        questionService.startReview(q.getId());
        questionService.approveQuestion(q.getId());
        questionService.publishQuestion(q.getId());

        List<Question> publishedForComp1 = questionService.getPublishedQuestionsByCompetency(competencyId1);
        assertFalse(publishedForComp1.isEmpty(), "Should find published question for competency 1");

        List<Question> publishedForComp2 = questionService.getPublishedQuestionsByCompetency(competencyId2);
        assertTrue(publishedForComp2.isEmpty(), "Competency 2 should have no published questions assigned");
    }
}
