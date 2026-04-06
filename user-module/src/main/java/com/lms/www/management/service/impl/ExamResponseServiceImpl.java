package com.lms.www.management.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.www.management.model.ExamAttempt;
import com.lms.www.management.model.ExamQuestion;
import com.lms.www.management.model.ExamResponse;
import com.lms.www.management.model.ExamSection;
import com.lms.www.management.model.Question;
import com.lms.www.management.model.QuestionOption;
import com.lms.www.management.repository.ExamAttemptRepository;
import com.lms.www.management.repository.ExamQuestionRepository;
import com.lms.www.management.repository.ExamResponseRepository;
import com.lms.www.management.repository.ExamSectionRepository;
import com.lms.www.management.repository.QuestionOptionRepository;
import com.lms.www.management.repository.QuestionRepository;
import com.lms.www.management.service.CodingExecutionService;
import com.lms.www.management.service.ExamResponseService;

@Service
@Transactional
public class ExamResponseServiceImpl implements ExamResponseService {

    private final ExamResponseRepository examResponseRepository;
    private final ExamAttemptRepository examAttemptRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final ExamSectionRepository examSectionRepository;
    private final QuestionOptionRepository questionOptionRepository;
    private final QuestionRepository questionRepository;
    private final CodingExecutionService codingExecutionService;

    public ExamResponseServiceImpl(
            ExamResponseRepository examResponseRepository,
            ExamAttemptRepository examAttemptRepository,
            ExamQuestionRepository examQuestionRepository,
            ExamSectionRepository examSectionRepository,
            QuestionOptionRepository questionOptionRepository,
            QuestionRepository questionRepository,
            CodingExecutionService codingExecutionService) {
        this.examResponseRepository = examResponseRepository;
        this.examAttemptRepository = examAttemptRepository;
        this.examQuestionRepository = examQuestionRepository;
        this.examSectionRepository = examSectionRepository;
        this.questionOptionRepository = questionOptionRepository;
        this.questionRepository = questionRepository;
        this.codingExecutionService = codingExecutionService; // ✅ IMPORTANT
    }

    // ================= SAVE / UPDATE RESPONSE =================
    @Override
    public ExamResponse saveOrUpdateResponse(
            Long attemptId,
            Long examQuestionId,
            Long questionId,
            Long selectedOptionId,
            String descriptiveAnswer,
            String codingSubmissionCode) {

        // 1️⃣ Fetch attempt
        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new IllegalStateException("Attempt not found"));

        if (!"IN_PROGRESS".equals(attempt.getStatus())) {
            throw new IllegalStateException("Cannot modify responses");
        }

        // 2️⃣ Resolve examQuestionId if questionId is provided
        if (examQuestionId == null && questionId != null) {
            java.util.Optional<ExamQuestion> eqOpt = examQuestionRepository
                    .findByExamIdAndQuestionId(attempt.getExamId(), questionId);
            if (eqOpt.isPresent()) {
                examQuestionId = eqOpt.get().getExamQuestionId();
            }
        }

        if (examQuestionId == null) {
            throw new IllegalStateException("Exam question not found for the provided questionId");
        }

        // 2️⃣ Fetch exam question
        ExamQuestion examQuestion = examQuestionRepository
                .findById(examQuestionId)
                .orElseThrow(() -> new IllegalStateException("Exam question not found"));

        // 3️⃣ Fetch exam section
        ExamSection examSection = examSectionRepository
                .findById(examQuestion.getExamSectionId())
                .orElseThrow(() -> new IllegalStateException("Exam section not found"));

        // 4️⃣ Validate ownership
        if (!examSection.getExamId().equals(attempt.getExamId())) {
            throw new IllegalStateException("Question does not belong to this exam");
        }

        // 5️⃣ Fetch response or create if it doesn't exist (fallback for older
        // attempts)
        final Long finalExamQuestionId = examQuestionId; // Ensure effectively final for lambda
        ExamResponse response = examResponseRepository
                .findByAttemptIdAndExamQuestionId(attemptId, finalExamQuestionId)
                .orElseGet(() -> {
                    ExamResponse newResponse = new ExamResponse();
                    newResponse.setAttemptId(attemptId);
                    newResponse.setExamQuestionId(finalExamQuestionId);
                    newResponse.setMarksAwarded(0.0);
                    return newResponse;
                });

        // 6️⃣ Fetch actual question
        Question question = questionRepository
                .findById(examQuestion.getQuestionId())
                .orElseThrow();

        // 🔒 STRICT TYPE-BASED SAVE
        switch (question.getQuestionType()) {

            case "MCQ":
                if (selectedOptionId == null) {
                    throw new IllegalStateException("Option required");
                }
                QuestionOption option = questionOptionRepository.findById(selectedOptionId)
                        .orElseThrow(() -> new IllegalStateException("Option not found"));

                if (!option.getQuestionId().equals(question.getQuestionId())) {
                    throw new IllegalStateException("Option does not belong to question");
                }
                response.setSelectedOptionId(selectedOptionId);
                response.setDescriptiveAnswer(null);
                response.setCodingSubmissionCode(null);

                // 🔥 AUTO EVALUATE MCQ IMMEDIATELY
                response.setMarksAwarded(
                        option.getIsCorrect() ? examQuestion.getMarks() : 0.0);
                response.setEvaluationType("AUTO");
                break;

            case "DESCRIPTIVE":
                if (descriptiveAnswer == null || descriptiveAnswer.isBlank()) {
                    throw new IllegalStateException("Answer required");
                }
                response.setDescriptiveAnswer(descriptiveAnswer);
                response.setSelectedOptionId(null);
                response.setCodingSubmissionCode(null);
                break;

            case "CODING":
                if (codingSubmissionCode == null || codingSubmissionCode.isBlank()) {
                    throw new IllegalStateException("Code required");
                }

                response.setCodingSubmissionCode(codingSubmissionCode);
                response.setSelectedOptionId(null);
                response.setDescriptiveAnswer(null);

                // ✅ SAVE FIRST (important)
                response = examResponseRepository.save(response);

                try {
                    // ✅ RUN CODING SAFELY
                    codingExecutionService.runSubmission(response.getResponseId());
                } catch (Exception e) {
                    System.err.println("Coding execution failed: " + e.getMessage());
                }

                // ✅ FETCH UPDATED MARKS
                response = examResponseRepository.findById(response.getResponseId())
                        .orElseThrow();

                break;

            default:
                throw new IllegalStateException("Unsupported question type");
        }

        response = examResponseRepository.save(response);

        // 🔥 AUTO UPDATE TOTAL SCORE + STATUS
        List<ExamResponse> allResponses = examResponseRepository.findByAttemptId(attemptId);

        double totalScore = allResponses.stream()
                .map(r -> r.getMarksAwarded() == null ? 0.0 : r.getMarksAwarded())
                .mapToDouble(Double::doubleValue)
                .sum();

        ExamAttempt attemptToUpdate = examAttemptRepository.findById(attemptId)
                .orElseThrow();

        attemptToUpdate.setScore(totalScore);

        // 🔥 THIS IS THE KEY FIX
        attemptToUpdate.setStatus("EVALUATED");

        examAttemptRepository.save(attemptToUpdate);

        return response;

    }

    // ================= AUTO EVALUATE MCQ =================
    @Override
    public void autoEvaluateMcq(Long attemptId) {

        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new IllegalStateException("Attempt not found"));

        List<ExamResponse> responses = examResponseRepository.findByAttemptId(attemptId);

        for (ExamResponse response : responses) {

            if (response.getSelectedOptionId() == null) {
                continue;
            }

            QuestionOption option = questionOptionRepository.findById(
                    response.getSelectedOptionId())
                    .orElseThrow();

            ExamQuestion examQuestion = examQuestionRepository.findById(
                    response.getExamQuestionId())
                    .orElseThrow();

            response.setMarksAwarded(
                    option.getIsCorrect() ? examQuestion.getMarks() : 0.0);

            response.setEvaluationType("AUTO");
            examResponseRepository.save(response);
        }
    }

    // ================= MANUAL EVALUATION =================
    @Override
    public ExamResponse evaluateResponse(
            Long attemptId,
            Long responseId,
            Double marks) {

        ExamResponse response = examResponseRepository.findById(responseId)
                .orElseThrow(() -> new IllegalStateException("Response not found"));

        if (!response.getAttemptId().equals(attemptId)) {
            throw new IllegalStateException("Response does not belong to this attempt");
        }

        ExamQuestion examQuestion = examQuestionRepository.findById(response.getExamQuestionId())
                .orElseThrow(() -> new IllegalStateException("Exam question not found"));

        Question question = questionRepository.findById(examQuestion.getQuestionId())
                .orElseThrow(() -> new IllegalStateException("Question not found"));

        if ("MCQ".equals(question.getQuestionType()) || "CODING".equals(question.getQuestionType())) {
            throw new IllegalStateException(
                    question.getQuestionType() + " questions are auto-evaluated and cannot be graded manually");
        }

        if (marks > examQuestion.getMarks()) {
            throw new IllegalStateException("Marks exceed max allowed");
        }

        response.setMarksAwarded(marks);
        response.setEvaluationType("MANUAL");

        ExamResponse savedResponse = examResponseRepository.save(response);

        // Update ExamAttempt score
        ExamAttempt attempt = examAttemptRepository.findById(attemptId).orElse(null);
        if (attempt != null) {
            Double totalScore = examResponseRepository.findByAttemptId(attemptId)
                    .stream()
                    .mapToDouble(r -> r.getMarksAwarded() == null ? 0.0 : r.getMarksAwarded())
                    .sum();
            attempt.setScore(totalScore);
            examAttemptRepository.save(attempt);
        }

        return savedResponse;
    }

    // ================= GET RESPONSES =================
    @Override
    public List<ExamResponse> getResponsesByAttempt(Long attemptId) {
        return examResponseRepository.findByAttemptId(attemptId);
    }

    // ================= DESCRIPTIVE VIEW =================
    @Override
    public List<Map<String, Object>> getDescriptiveResponsesForEvaluation(Long attemptId) {

        List<ExamResponse> responses = examResponseRepository.findByAttemptId(attemptId);

        List<Map<String, Object>> result = new ArrayList<>();

        for (ExamResponse response : responses) {

            ExamQuestion examQuestion = examQuestionRepository.findById(
                    response.getExamQuestionId())
                    .orElseThrow();

            Question question = questionRepository.findById(
                    examQuestion.getQuestionId())
                    .orElseThrow();

            if (!"DESCRIPTIVE".equals(question.getQuestionType())) {
                continue;
            }

            Map<String, Object> row = new HashMap<>();
            row.put("responseId", response.getResponseId());
            row.put("questionText", question.getQuestionText());
            row.put("maxMarks", examQuestion.getMarks());
            row.put("studentAnswer", response.getDescriptiveAnswer());
            row.put("marksAwarded", response.getMarksAwarded());

            result.add(row);
        }

        return result;
    }

    // ================= CODING VIEW =================
    @Override
    public List<Map<String, Object>> getCodingResponsesForEvaluation(Long attemptId) {

        List<ExamResponse> responses = examResponseRepository.findByAttemptId(attemptId);

        List<Map<String, Object>> result = new ArrayList<>();

        for (ExamResponse response : responses) {

            if (response.getCodingSubmissionCode() == null) {
                continue;
            }

            ExamQuestion examQuestion = examQuestionRepository.findById(
                    response.getExamQuestionId())
                    .orElseThrow();

            Question question = questionRepository.findById(
                    examQuestion.getQuestionId())
                    .orElseThrow();

            if (!"CODING".equals(question.getQuestionType())) {
                continue;
            }

            Map<String, Object> row = new HashMap<>();
            row.put("responseId", response.getResponseId());
            row.put("questionText", question.getQuestionText());
            row.put("maxMarks", examQuestion.getMarks());
            row.put("submissionCode", response.getCodingSubmissionCode());
            row.put("marksAwarded", response.getMarksAwarded());

            result.add(row);
        }

        return result;
    }
}