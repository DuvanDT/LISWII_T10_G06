package com.example.questionbank;

import com.example.questionbank.api.dto.*;
import com.example.questionbank.domain.model.DifficultyLevel;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * End-to-end REST API integration tests for QuestionController and QuizController.
 */
@SpringBootTest
@AutoConfigureMockMvc
class QuestionBankApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("API Test: Complete REST lifecycle workflow from Question creation to Quiz inclusion")
    void testFullRestApiWorkflow() throws Exception {
        // 1. Create a Multiple-Choice Question (Uses seeded author ID 1 and topic ID 1 from DataInitializer)
        CreateMultipleChoiceQuestionRequest createReq = new CreateMultipleChoiceQuestionRequest();
        createReq.setStatement("What is the primary role of a domain repository contract in clean persistence?");
        createReq.setDifficulty(DifficultyLevel.MEDIUM);
        createReq.setAuthorId(1L); // author_ana
        createReq.setTopicId(1L);  // Software Architecture
        createReq.setCompetencyIds(List.of(1L)); // COMP-01
        createReq.setMetadata(new QuestionMetadataDto("Analysis", 120, "Spring Data decoupled"));
        createReq.setOptions(List.of(
            new AnswerOptionDto("Expresses persistence operations independently of database technology", "Correct", true),
            new AnswerOptionDto("Directly extends JpaRepository inside the domain package", "Violates DIP", false),
            new AnswerOptionDto("Annotates entities with Hibernate @Table definitions", "Contaminates domain", false),
            new AnswerOptionDto("Forces all entities to be persistent", "Incorrect", false),
            new AnswerOptionDto("Implements HTTP controllers", "Presentation concern", false)
        ));

        String responseBody = mockMvc.perform(post("/api/questions/multiple-choice")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createReq)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.status").value("DRAFT"))
            .andReturn().getResponse().getContentAsString();

        Number questionIdNum = com.jayway.jsonpath.JsonPath.read(responseBody, "$.id");
        Long questionId = questionIdNum.longValue();

        // 2. Query questions
        mockMvc.perform(get("/api/questions/" + questionId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.statement").value("What is the primary role of a domain repository contract in clean persistence?"));

        // 3. Submit question to review (DRAFT -> PENDING_REVIEW)
        mockMvc.perform(post("/api/questions/" + questionId + "/submit-review"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("PENDING_REVIEW"));

        // 4. Start review (PENDING_REVIEW -> IN_REVIEW)
        mockMvc.perform(post("/api/questions/" + questionId + "/start-review"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("IN_REVIEW"));

        // 5. Approve question (IN_REVIEW -> APPROVED)
        mockMvc.perform(post("/api/questions/" + questionId + "/approve"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("APPROVED"));

        // 6. Attempt to add to Quiz while only APPROVED (not PUBLISHED) -> Expect 400 Bad Request
        CreateQuizRequest invalidQuizReq = new CreateQuizRequest();
        invalidQuizReq.setTitle("Early Simulation");
        invalidQuizReq.setDescription("Attempting to include non-published question");
        invalidQuizReq.setCreatorId(3L); // teacher_maria
        invalidQuizReq.setQuestionIds(List.of(questionId));

        mockMvc.perform(post("/api/quizzes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidQuizReq)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Business Rule Violation"));

        // 7. Publish question (APPROVED -> PUBLISHED)
        mockMvc.perform(post("/api/questions/" + questionId + "/publish"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("PUBLISHED"));

        // 8. Create Quiz with PUBLISHED question -> Must succeed (201 Created)
        CreateQuizRequest validQuizReq = new CreateQuizRequest();
        validQuizReq.setTitle("Saber Pro 2026 Simulation Test");
        validQuizReq.setDescription("Final official evaluation quiz");
        validQuizReq.setCreatorId(3L); // teacher_maria
        validQuizReq.setQuestionIds(List.of(questionId));

        mockMvc.perform(post("/api/quizzes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validQuizReq)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.title").value("Saber Pro 2026 Simulation Test"))
            .andExpect(jsonPath("$.questionIds[0]").value(questionId));
    }
}
