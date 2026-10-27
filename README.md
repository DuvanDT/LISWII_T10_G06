# Saber Pro Question Bank (`saberpro-question-bank`)
**Software Engineering Laboratory II — Term 2-2026**  
**Workshop 10: Java Persistence API (JPA)**  
**University of Cauca**
**Duvan Alexis Hoyos y Juan José Hurtado**

---

## 1. System Overview and Purpose

The **Saber Pro Question Bank** is a layered Spring Boot application designed to author, classify, review, publish, and bundle academic evaluation questions for the Colombian Saber Pro state examinations.

The system is built strictly adhering to the **Clean Persistence** architectural pattern:
- **Domain Independence:** Domain entities and business rules are 100% pure Java POJOs, completely free of technological annotations (`jakarta.persistence.*`, `@Entity`, `@Table`).
- **Dependency Inversion (DIP):** Application use cases depend exclusively on abstract repository contracts defined in the domain layer.
- **Encapsulated Infrastructure:** Relational JPA mappings, entity records (`*Record`), Hibernate annotations, and Spring Data interfaces remain isolated within the infrastructure layer.
- **Explicit Translation:** A dedicated mapper (`QuestionDomainMapper`) translates bi-directionally between domain entities and JPA persistence records.

---

## 2. Architecture & Package Structure

```text
src/main/java/com/example/questionbank/
├── api/                                        <-- Presentation Layer (HTTP REST)
│   ├── QuestionController.java                 <-- Endpoints for questions & review lifecycle
│   ├── QuizController.java                     <-- Endpoints for evaluation quizzes
│   ├── GlobalExceptionHandler.java             <-- Standardized 400 Bad Request error responses
│   └── dto/                                    <-- Decoupled request & response DTOs
│
├── application/                                <-- Application Layer (Use Cases & Transactions)
│   ├── QuestionService.java                    <-- Use Cases 1-4: authoring, query, classify, review
│   └── QuizService.java                        <-- Use Case 5: quiz bundling & publication validation
│
├── domain/                                     <-- Domain Layer (Pure Business Core)
│   ├── model/                                  <-- Pure POJO Entities, Invariants & Enums
│   │   ├── Question.java (abstract)            <-- Base entity with state machine invariants
│   │   ├── MultipleChoiceQuestion.java         <-- Specialization with AnswerOptions
│   │   ├── TrueFalseQuestion.java              <-- Specialization with expected truth value
│   │   ├── QuestionMetadata.java               <-- Cognitive level, response time, editorial notes
│   │   ├── AnswerOption.java                   <-- Distractors and correct answer key
│   │   ├── User.java, Topic.java, Competency.java
│   │   ├── Quiz.java                           <-- Evaluation quiz grouping published questions
│   │   └── QuestionStatus.java, DifficultyLevel.java, UserRole.java
│   └── repository/                             <-- Technology-Independent Repository Contracts
│       ├── QuestionRepository.java
│       ├── UserRepository.java
│       ├── TopicRepository.java
│       ├── CompetencyRepository.java
│       └── QuizRepository.java
│
└── infrastructure/                             <-- Infrastructure Layer (JPA & Relational Storage)
    ├── DataInitializer.java                   <-- Automatic baseline seeding on startup
    └── jpa/
        ├── record/                             <-- JPA Entities with relational annotations
        │   ├── QuestionRecord.java             <-- Root entity with @Inheritance(strategy = JOINED)
        │   ├── MultipleChoiceQuestionRecord.java
        │   ├── TrueFalseQuestionRecord.java
        │   ├── QuestionMetadataRecord.java     <-- @OneToOne owning side (question_id UNIQUE)
        │   ├── AnswerOptionRecord.java         <-- @ManyToOne owning side (question_id)
        │   ├── UserRecord.java, TopicRecord.java, CompetencyRecord.java
        │   └── QuizRecord.java                 <-- @ManyToMany with QuestionRecord (quiz_questions)
        ├── repository/                         <-- Spring Data JPA Data Interfaces
        │   ├── SpringQuestionDataRepository.java
        │   ├── SpringUserDataRepository.java
        │   ├── SpringTopicDataRepository.java
        │   ├── SpringCompetencyDataRepository.java
        │   └── SpringQuizDataRepository.java
        ├── mapper/
        │   └── QuestionDomainMapper.java       <-- Bidirectional Domain <-> JPA Record Translator
        ├── QuestionRepositoryJpa.java          <-- Concrete adapter for QuestionRepository
        ├── QuizRepositoryJpa.java
        ├── UserRepositoryJpa.java
        ├── TopicRepositoryJpa.java
        └── CompetencyRepositoryJpa.java
```

---

## 3. Relational Persistence Mappings

| Relational Concept | JPA Mapping in Infrastructure | Key Design Decisions |
|---|---|---|
| **One-to-One (`1:1`)** | `QuestionMetadataRecord.question` uses `@OneToOne` and `@JoinColumn(name = "question_id", unique = true, nullable = false)`. | `QuestionMetadataRecord` is the owning side. `unique = true` guarantees at database level that each question has at most one metadata record. Managed with `orphanRemoval = true` and `CascadeType.ALL`. |
| **One-to-Many (`1:N`)** | `MultipleChoiceQuestionRecord.options` uses `@OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)`. | `AnswerOptionRecord` is the owning side with `@ManyToOne`. Deleting a question cascades to all its answer options. Domain validates single correct key. |
| **Many-to-One (`N:1`)** | `QuestionRecord.author` and `QuestionRecord.topic` use `@ManyToOne` with `@JoinColumn(nullable = false)`. | Questions must be associated with valid authors and topics; foreign keys prevent dangling questions. |
| **Many-to-Many (`N:M`)** | `QuestionRecord.competencies` uses `@ManyToMany` and join table `question_competencies`. | Evaluates multiple competencies per question and allows competencies to span multiple questions. |
| **Inheritance (`JOINED`)** | `QuestionRecord` + `MultipleChoiceQuestionRecord` + `TrueFalseQuestionRecord` use `@Inheritance(strategy = InheritanceType.JOINED)`. | Normalization to 3NF. Shared attributes stored in base table `questions`; subtype-specific columns stored in `multiple_choice_questions` and `true_false_questions`. Foreign keys point cleanly to `questions(id)`. |

---

## 4. Requirements & Setup

* **Java Development Kit:** JDK 17 or higher (tested on OpenJDK 25 LTS).
* **Build Tool:** Apache Maven 3.8+.
* **Database:** Embedded H2 in-memory (`create-drop` per session).

Verify your environment:
```bash
java -version
mvn -version
```

---

## 5. Build, Test and Run

From the project directory (`saberpro-question-bank`):

### 5.1 Run Automated Tests (Unit + JPA Integration + Web API)
```bash
mvn clean test
```
*Executes all 11 automated test cases, verifying domain invariants, JPA persistence, polymorphic queries, and REST status codes.*

### 5.2 Start the Application Server
```bash
mvn spring-boot:run
```
The REST API starts listening at `http://localhost:8080`.

### 5.3 H2 Database Web Console
Access the H2 web console in your browser:
* **URL:** `http://localhost:8080/h2-console`
* **Driver Class:** `org.h2.Driver`
* **JDBC URL:** `jdbc:h2:mem:questionbankdb`
* **User Name:** `sa`
* **Password:** *(leave blank)*

---

## 6. Seeded Test Data

Upon startup, `DataInitializer` automatically inserts standard test fixtures if tables are empty:
* **Users:**
  - `ID: 1` $\rightarrow$ `author_ana` (Role: `AUTHOR`, Email: `ana@unicauca.edu.co`)
  - `ID: 2` $\rightarrow$ `reviewer_carlos` (Role: `REVIEWER`, Email: `carlos@unicauca.edu.co`)
  - `ID: 3` $\rightarrow$ `teacher_maria` (Role: `TEACHER`, Email: `maria@unicauca.edu.co`)
  - `ID: 4` $\rightarrow$ `admin_wilson` (Role: `ADMIN`, Email: `admin@unicauca.edu.co`)
* **Topics:**
  - `ID: 1` $\rightarrow$ *Software Architecture*
  - `ID: 2` $\rightarrow$ *Database Systems*
* **Competencies:**
  - `ID: 1` $\rightarrow$ `COMP-01`: *Design and Modeling*
  - `ID: 2` $\rightarrow$ `COMP-02`: *Critical Analysis*

---

## 7. API Examples for the 5 Use Cases (Punto 8)

### Use Case 1: Create Questions

#### A. Create a Multiple-Choice Question (with 4 distractors + 1 correct option and metadata):
```bash
curl -X POST http://localhost:8080/api/questions/multiple-choice \
  -H "Content-Type: application/json" \
  -d '{
    "statement": "Which design principle recommends that high-level modules should depend on abstractions?",
    "difficulty": "MEDIUM",
    "authorId": 1,
    "topicId": 1,
    "competencyIds": [1, 2],
    "metadata": {
      "cognitiveLevel": "Analysis",
      "estimatedTimeSeconds": 90,
      "editorialNotes": "SOLID principles assessment"
    },
    "options": [
      {"text": "Dependency Inversion Principle (DIP)", "explanation": "Correct answer according to SOLID", "correct": true},
      {"text": "Single Responsibility Principle (SRP)", "explanation": "Distractor 1: deals with reasons to change", "correct": false},
      {"text": "Open/Closed Principle (OCP)", "explanation": "Distractor 2: deals with extension vs modification", "correct": false},
      {"text": "Liskov Substitution Principle (LSP)", "explanation": "Distractor 3: deals with subtype substitutability", "correct": false},
      {"text": "Interface Segregation Principle (ISP)", "explanation": "Distractor 4: deals with client-specific interfaces", "correct": false}
    ]
  }'
```

#### B. Create a True/False Question:
```bash
curl -X POST http://localhost:8080/api/questions/true-false \
  -H "Content-Type: application/json" \
  -d '{
    "statement": "In clean persistence architecture, domain models must import jakarta.persistence annotations.",
    "difficulty": "EASY",
    "authorId": 1,
    "topicId": 1,
    "expectedValue": false,
    "justification": "Domain entities must remain strictly technology-independent.",
    "competencyIds": [1],
    "metadata": {
      "cognitiveLevel": "Comprehension",
      "estimatedTimeSeconds": 45,
      "editorialNotes": "Introductory architecture question"
    }
  }'
```

---

### Use Case 2: Query Questions

#### A. List all questions (Polymorphic retrieval):
```bash
curl http://localhost:8080/api/questions
```

#### B. Query question by ID:
```bash
curl http://localhost:8080/api/questions/1
```

#### C. List questions by Author:
```bash
curl http://localhost:8080/api/questions/author/1
```

#### D. List questions by Status:
```bash
curl http://localhost:8080/api/questions/status/DRAFT
```

#### E. Query published questions evaluating a specific Competency:
```bash
curl http://localhost:8080/api/questions/competency/1/published
```

---

### Use Case 3: Classify a Question (Update Topic & Competencies)
```bash
curl -X PUT http://localhost:8080/api/questions/1/classify \
  -H "Content-Type: application/json" \
  -d '{
    "topicId": 2,
    "competencyIds": [1, 2]
  }'
```

---

### Use Case 4: Question Review & Publication Lifecycle

#### Step 4.1: Submit Question for Review (`DRAFT` $\rightarrow$ `PENDING_REVIEW`)
*Enforces domain invariants: verifies non-empty statement, topic, competencies, metadata, and answer options.*
```bash
curl -X POST http://localhost:8080/api/questions/1/submit-review
```

#### Step 4.2: Start Review (`PENDING_REVIEW` $\rightarrow$ `IN_REVIEW`)
```bash
curl -X POST http://localhost:8080/api/questions/1/start-review
```

#### Step 4.3 (Optional): Reject Question back to Author (`IN_REVIEW` $\rightarrow$ `DRAFT`)
```bash
curl -X POST http://localhost:8080/api/questions/1/reject \
  -H "Content-Type: application/json" \
  -d '{"feedback":"Option C explanation requires further technical detail."}'
```

#### Step 4.4: Approve Question (`IN_REVIEW` $\rightarrow$ `APPROVED`)
```bash
curl -X POST http://localhost:8080/api/questions/1/approve
```

#### Step 4.5: Publish Question (`APPROVED` $\rightarrow$ `PUBLISHED`)
```bash
curl -X POST http://localhost:8080/api/questions/1/publish
```

---

### Use Case 5: Create a Quiz (Cuestionario / Simulacro)

#### A. Attempting to add a NON-PUBLISHED question (Business Rule Rejection Demonstration):
```bash
curl -X POST http://localhost:8080/api/quizzes \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Premature Simulation",
    "description": "Demonstrates rejection of draft questions",
    "creatorId": 3,
    "questionIds": [2]
  }'
```
*Response: `400 Bad Request` with `{"error":"Business Rule Violation","message":"Cannot add question ID 2 to quiz: Only PUBLISHED questions are allowed..."}`.*

#### B. Creating a Quiz with PUBLISHED questions (Success):
```bash
curl -X POST http://localhost:8080/api/quizzes \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Saber Pro Official Engineering Simulation 2026",
    "description": "Evaluates software architecture and relational persistence",
    "creatorId": 3,
    "questionIds": [1]
  }'
```

#### C. Retrieve All Quizzes:
```bash
curl http://localhost:8080/api/quizzes
```
