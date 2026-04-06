package com.lms.www.management.service.impl;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.www.management.dashboard.dto.StudentExamAttemptResultDTO;
import com.lms.www.management.dashboard.dto.StudentExamQuestionDTO;
import com.lms.www.management.dashboard.dto.StudentExamResponseSaveDTO;
import com.lms.www.management.dashboard.dto.StudentExamSectionDTO;
import com.lms.www.management.dashboard.dto.StudentQuestionOptionDTO;
import com.lms.www.management.model.Exam;
import com.lms.www.management.model.ExamAttempt;
import com.lms.www.management.model.ExamQuestion;
import com.lms.www.management.model.ExamResponse;
import com.lms.www.management.model.ExamSection;
import com.lms.www.management.repository.ExamAttemptRepository;
import com.lms.www.management.repository.ExamQuestionRepository;
import com.lms.www.management.repository.ExamRepository;
import com.lms.www.management.repository.ExamResponseRepository;
import com.lms.www.management.repository.ExamSectionRepository;
import com.lms.www.management.repository.QuestionOptionRepository;
import com.lms.www.management.repository.StudentBatchRepository;
import com.lms.www.management.service.StudentExamService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentExamServiceImpl implements StudentExamService {

    private final ExamRepository examRepository;
    private final ExamAttemptRepository examAttemptRepository;
    private final ExamResponseRepository examResponseRepository;
    private final StudentBatchRepository studentBatchRepository;
    private final QuestionOptionRepository questionOptionRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final ExamSectionRepository examSectionRepository;

    private Exam validateStudentAccessToExam(Long examId, Long studentId) {
        List<Long> batchIds = studentBatchRepository.findByStudentId(studentId).stream()
                .map(sb -> sb.getBatchId())
                .collect(Collectors.toList());

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        if (exam.getBatchId() != null && !batchIds.contains(exam.getBatchId())) {
            throw new RuntimeException("Access Denied: You do not have access to this exam via your active batches.");
        }
        return exam;
    }

    @Override
    public List<Exam> getAvailableExamsForStudent(Long studentId) {
        List<Long> batchIds = studentBatchRepository.findByStudentId(studentId).stream()
                .map(sb -> sb.getBatchId())
                .collect(Collectors.toList());
        return examRepository.findByBatchIdInAndIsDeletedFalse(batchIds);
    }

    @Override
    public Exam getExamDetails(Long examId, Long studentId) {
        return validateStudentAccessToExam(examId, studentId);
    }

    @Override
    public List<StudentExamSectionDTO> getExamQuestions(Long examId, Long studentId) {
        validateStudentAccessToExam(examId, studentId);

        List<ExamSection> sections = examSectionRepository.findByExamIdOrderBySectionOrderAsc(examId);

        return sections.stream().map(section -> {
            List<ExamQuestion> questions = examQuestionRepository
                    .findByExamSectionIdOrderByQuestionOrderAsc(section.getExamSectionId());

            List<StudentExamQuestionDTO> questionDTOs = questions.stream().map(eq -> {
                com.lms.www.management.model.Question q = eq.getQuestion();

                List<StudentQuestionOptionDTO> optionDTOs = questionOptionRepository.findByQuestionId(q.getQuestionId())
                        .stream()
                        .map(opt -> StudentQuestionOptionDTO.builder()
                                .optionId(opt.getOptionId())
                                .questionId(opt.getQuestionId())
                                .optionText(opt.getOptionText())
                                .optionImageUrl(opt.getOptionImageUrl())
                                .build())
                        .collect(Collectors.toList());

                return StudentExamQuestionDTO.builder()
                        .examQuestionId(eq.getExamQuestionId())
                        .questionId(q.getQuestionId())
                        .questionText(q.getQuestionText())
                        .questionImageUrl(q.getQuestionImageUrl())
                        .questionType(q.getQuestionType())
                        .contentType(q.getContentType().name())
                        .programmingLanguage(q.getProgrammingLanguage())
                        .marks(eq.getMarks())
                        .questionOrder(eq.getQuestionOrder())
                        .options(optionDTOs)
                        .build();
            }).collect(Collectors.toList());

            return StudentExamSectionDTO.builder()
                    .examSectionId(section.getExamSectionId())
                    .sectionId(section.getSectionId())
                    .sectionOrder(section.getSectionOrder())
                    .shuffleQuestions(section.getShuffleQuestions())
                    .questions(questionDTOs)
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public ExamAttempt getActiveAttempt(Long examId, Long studentId) {
        return examAttemptRepository.findFirstByExamIdAndStudentIdAndStatus(examId, studentId, "IN_PROGRESS")
                .orElseThrow(() -> new RuntimeException("No active attempt found."));
    }

    @Override
    @Transactional
    public ExamAttempt startExamAttempt(Long examId, Long studentId) {
        validateStudentAccessToExam(examId, studentId);

        examAttemptRepository.findFirstByExamIdAndStudentIdAndStatus(examId, studentId, "IN_PROGRESS")
                .ifPresent(attempt -> {
                    throw new RuntimeException("Another attempt is already in progress.");
                });

        int previousAttempts = examAttemptRepository.countByExamIdAndStudentId(examId, studentId);
        int maxAttempts = 3; // TODO: Replace with exam.getMaxAttempts() when added to DB

        if (previousAttempts >= maxAttempts) {
            log.warn("Student {} exceeded max attempts ({}) for Exam {}", studentId, maxAttempts, examId);
            throw new RuntimeException("Maximum attempts reached for this exam.");
        }

        ExamAttempt newAttempt = new ExamAttempt();
        newAttempt.setExamId(examId);
        newAttempt.setStudentId(studentId);
        newAttempt.setAttemptNumber(previousAttempts + 1);
        newAttempt.setStartTime(LocalDateTime.now());
        newAttempt.setStatus("IN_PROGRESS");
        newAttempt.setScore(0.0);

        log.info("Student {} started Exam {} (Attempt #{}).", studentId, examId, newAttempt.getAttemptNumber());
        return examAttemptRepository.save(newAttempt);
    }

    @Override
    @Transactional
    public ExamResponse saveExamResponse(Long examId, Long studentId, StudentExamResponseSaveDTO saveDTO) {
        Exam exam = validateStudentAccessToExam(examId, studentId);

        List<ExamAttempt> attempts = examAttemptRepository.findByStudentId(studentId);
        ExamAttempt attempt = attempts.stream()
                .filter(a -> a.getExamId().equals(examId))
                .max(Comparator.comparing(ExamAttempt::getStartTime))
                .orElseThrow(() -> new RuntimeException("No active attempt found for this exam."));

        if (!"IN_PROGRESS".equals(attempt.getStatus())) {
            throw new IllegalStateException("Exam already submitted.");
        }

        // Timer validation: block saving if time is up
        if (attempt.getStartTime().plusMinutes(exam.getDurationMinutes()).isBefore(LocalDateTime.now())) {
            attempt.setStatus("AUTO_SUBMITTED");
            attempt.setEndTime(attempt.getStartTime().plusMinutes(exam.getDurationMinutes()));
            examAttemptRepository.save(attempt);
            log.info("Auto-submitted Exam {} for Student {} due to time limit.", examId, studentId);
            throw new RuntimeException("Time is up. Exam has been auto-submitted and locked for further answers.");
        }

        ExamResponse response = examResponseRepository
                .findByAttemptIdAndExamQuestionId(attempt.getAttemptId(), saveDTO.getExamQuestionId())
                .orElse(new ExamResponse());

        response.setAttemptId(attempt.getAttemptId());
        response.setExamQuestionId(saveDTO.getExamQuestionId());
        response.setSelectedOptionId(saveDTO.getSelectedOptionId());
        response.setDescriptiveAnswer(saveDTO.getDescriptiveAnswer());
        response.setCodingSubmissionCode(saveDTO.getCodingSubmissionCode());

        return examResponseRepository.save(response);
    }

    @Override
    @Transactional
    public StudentExamAttemptResultDTO submitExamAttempt(Long examId, Long studentId) {
        Exam exam = validateStudentAccessToExam(examId, studentId);

        ExamAttempt attempt = examAttemptRepository
                .findFirstByExamIdAndStudentIdAndStatus(examId, studentId, "IN_PROGRESS")
                .orElse(null);

        // Double Call Prevention mechanism (Covering both SUBMITTED and AUTO_SUBMITTED
        // identically)
        if (attempt == null) {
            ExamAttempt lastAttempt = examAttemptRepository
                    .findTopByStudentIdAndExamIdAndStatusOrderByScoreDesc(studentId, examId, "SUBMITTED").orElse(null);
            if (lastAttempt == null) {
                lastAttempt = examAttemptRepository
                        .findTopByStudentIdAndExamIdAndStatusOrderByScoreDesc(studentId, examId, "AUTO_SUBMITTED")
                        .orElse(null);
            }
            if (lastAttempt != null) {
                return buildResultDTO(lastAttempt, exam);
            }
            throw new RuntimeException("No active attempt found to submit.");
        }

        attempt.setStatus("SUBMITTED");
        attempt.setEndTime(LocalDateTime.now());

        double totalScore = 0.0;
        List<ExamResponse> responses = examResponseRepository.findByAttemptId(attempt.getAttemptId());
        for (ExamResponse res : responses) {
            if (res.getSelectedOptionId() != null) {
                questionOptionRepository.findById(res.getSelectedOptionId()).ifPresent(opt -> {
                    if (opt.getIsCorrect()) {
                        examQuestionRepository.findById(res.getExamQuestionId()).ifPresent(eq -> {
                            res.setMarksAwarded(eq.getMarks());
                            res.setEvaluationType("AUTO");
                            examResponseRepository.save(res);
                        });
                    } else {
                        // TODO: Evaluate negative marks in the future (e.g., eq.getNegativeMarks())
                        res.setMarksAwarded(0.0);
                        res.setEvaluationType("AUTO");
                        examResponseRepository.save(res);
                    }
                });
                if (res.getMarksAwarded() != null) {
                    totalScore += res.getMarksAwarded();
                }
            } else if (res.getDescriptiveAnswer() != null || res.getCodingSubmissionCode() != null) {
                res.setEvaluationType("MANUAL");
                examResponseRepository.save(res);
            }
        }

        // TODO: Advanced Section-wise score evaluation can be hooked in here later
        attempt.setScore(totalScore);
        attempt = examAttemptRepository.save(attempt);

        log.info("Student {} submitted Exam {} with status {} and score {}.", studentId, examId, attempt.getStatus(),
                totalScore);

        return buildResultDTO(attempt, exam);
    }

    @Override
    public List<StudentExamAttemptResultDTO> getStudentExamAttempts(Long studentId) {
        List<ExamAttempt> attempts = examAttemptRepository.findByStudentId(studentId);
        return attempts.stream().map(attempt -> {
            Exam exam = examRepository.findById(attempt.getExamId()).orElse(null);
            return buildResultDTO(attempt, exam);
        }).collect(Collectors.toList());
    }

    private StudentExamAttemptResultDTO buildResultDTO(ExamAttempt attempt, Exam exam) {
        double percentage = 0.0;
        boolean isPassed = false;

        if (exam != null && exam.getTotalMarks() != null && exam.getTotalMarks() > 0) {
            percentage = (attempt.getScore() / exam.getTotalMarks()) * 100;
            if (exam.getPassPercentage() != null && percentage >= exam.getPassPercentage()) {
                isPassed = true;
            }
        }

        return StudentExamAttemptResultDTO.builder()
                .attemptId(attempt.getAttemptId())
                .examId(attempt.getExamId())
                .examTitle(exam != null ? exam.getTitle() : "Unknown Exam")
                .attemptNumber(attempt.getAttemptNumber())
                .startTime(attempt.getStartTime())
                .endTime(attempt.getEndTime())
                .status(attempt.getStatus()) // Covers IN_PROGRESS, SUBMITTED, AUTO_SUBMITTED identically
                .obtainedMarks(attempt.getScore())
                .totalMarks(exam != null ? exam.getTotalMarks() : 0)
                .percentage(percentage)
                .isPassed(isPassed)
                .build();
    }
}