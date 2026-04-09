package com.lms.www.management.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lms.www.management.enums.ProgrammingLanguage;
import com.lms.www.management.model.ContentType;
import com.lms.www.management.model.Question;
import com.lms.www.management.model.QuestionDescriptiveAnswer;
import com.lms.www.management.repository.QuestionDescriptiveAnswerRepository;
import com.lms.www.management.repository.QuestionRepository;
import com.lms.www.management.util.FileUploadUtil;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionRepository questionRepository;
    private final QuestionDescriptiveAnswerRepository descriptiveRepository;

    public QuestionController(
            QuestionRepository questionRepository,
            QuestionDescriptiveAnswerRepository descriptiveRepository) {

        this.questionRepository = questionRepository;
        this.descriptiveRepository = descriptiveRepository;
    }

    // ================= CREATE QUESTION (JSON) =================
    @PostMapping(consumes = "application/json", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('QUESTION_BANK_MANAGE', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR')")
    public ResponseEntity<Question> createQuestionJson(
            @RequestBody Map<String, Object> request) {

        String questionText = (String) request.get("questionText");
        String questionType = (String) request.get("questionType");
        String contentTypeStr = (String) request.get("contentType");
        String programmingLanguageStr =
                (String) request.get("programmingLanguage");

        if (questionText == null || questionType == null || contentTypeStr == null) {
            throw new IllegalStateException("Missing required fields");
        }

        Question question = new Question();
        question.setQuestionText(questionText);
        question.setQuestionType(questionType);
        question.setContentType(ContentType.valueOf(contentTypeStr));

        if ("CODING".equalsIgnoreCase(questionType)) {

            if (programmingLanguageStr == null) {
                throw new IllegalStateException(
                        "Programming language required for CODING question");
            }

            question.setProgrammingLanguage(
                    ProgrammingLanguage.valueOf(programmingLanguageStr));
        } else {
            question.setProgrammingLanguage(null);
        }

        Question savedQuestion = questionRepository.save(question);

        if ("DESCRIPTIVE".equalsIgnoreCase(questionType)) {

            String modelAnswer = (String) request.get("modelAnswer");
            String keywords = (String) request.get("keywords");

            if (modelAnswer != null && !modelAnswer.isBlank()) {

                QuestionDescriptiveAnswer descriptive =
                        new QuestionDescriptiveAnswer();

                descriptive.setQuestionId(savedQuestion.getQuestionId());
                descriptive.setAnswerText(modelAnswer);
                descriptive.setGuidelines(keywords);
                descriptive.setCreatedAt(LocalDateTime.now());

                descriptiveRepository.save(descriptive);
            }
        }

        return ResponseEntity.ok(savedQuestion);
    }

    // ================= CREATE QUESTION (MULTIPART) =================
    @PostMapping(consumes = "multipart/form-data", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('QUESTION_BANK_MANAGE', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR')")
    public ResponseEntity<Question> createQuestionMultipart(

            @RequestParam(required = false) String questionText,
            @RequestParam String questionType,
            @RequestParam ContentType contentType,
            @RequestParam(required = false) MultipartFile questionImage

    ) {

        Question question = new Question();
        question.setQuestionText(questionText);
        question.setQuestionType(questionType);
        question.setContentType(contentType);

        if (questionImage != null && !questionImage.isEmpty()) {

            String imageUrl = FileUploadUtil
                    .saveFile(questionImage, "questions");

            question.setQuestionImageUrl(imageUrl);
        }

        return ResponseEntity.ok(
                questionRepository.save(question)
        );
    }

    // ================= GET ALL QUESTIONS =================
    @GetMapping
    @PreAuthorize("hasAnyAuthority('QUESTION_BANK_VIEW', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR')")
    public ResponseEntity<List<Question>> getAllQuestions() {
        return ResponseEntity.ok(
                questionRepository.findAll()
        );
    }

    // ================= GET QUESTION BY ID =================
    @GetMapping("/{questionId}")
    @PreAuthorize("hasAnyAuthority('QUESTION_BANK_VIEW', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR')")
    public ResponseEntity<Question> getQuestion(
            @PathVariable Long questionId) {

        return ResponseEntity.ok(
                questionRepository.findById(questionId)
                        .orElseThrow(() ->
                                new IllegalStateException("Question not found"))
        );
    }

    // ================= DELETE QUESTION =================
    @DeleteMapping("/{questionId}")
    @PreAuthorize("hasAnyAuthority('QUESTION_BANK_MANAGE', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR')")
    public ResponseEntity<Void> deleteQuestion(
            @PathVariable Long questionId) {

        questionRepository.deleteById(questionId);
        return ResponseEntity.noContent().build();
    }

    // ================= GET DESCRIPTIVE DETAILS =================
    @GetMapping("/{questionId}/descriptive-details")
    @PreAuthorize("hasAnyAuthority('QUESTION_BANK_VIEW', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR')")
    public ResponseEntity<?> getDescriptiveDetails(
            @PathVariable Long questionId) {

        QuestionDescriptiveAnswer descriptive =
                descriptiveRepository.findByQuestionId(questionId)
                        .orElseThrow(() ->
                                new IllegalStateException("Descriptive details not found"));

        return ResponseEntity.ok(
                Map.of(
                        "modelAnswer", descriptive.getAnswerText(),
                        "keywords", descriptive.getGuidelines()
                )
        );
    }
}