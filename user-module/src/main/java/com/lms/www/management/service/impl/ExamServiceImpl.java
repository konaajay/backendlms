package com.lms.www.management.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.www.common.exception.ResourceNotFoundException;
import com.lms.www.management.model.Exam;
import com.lms.www.management.repository.ExamRepository;
import com.lms.www.management.repository.ExamQuestionRepository;
import com.lms.www.management.repository.BatchRepository;
import com.lms.www.management.repository.CourseRepository;
import com.lms.www.management.service.ExamService;

@Service
@Transactional
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final BatchRepository batchRepository;
    private final CourseRepository courseRepository;
    private final com.lms.www.management.util.SecurityUtil securityUtil;

    public ExamServiceImpl(ExamRepository examRepository, 
                           ExamQuestionRepository examQuestionRepository,
                           BatchRepository batchRepository,
                           CourseRepository courseRepository,
                           com.lms.www.management.util.SecurityUtil securityUtil) {
        this.examRepository = examRepository;
        this.examQuestionRepository = examQuestionRepository;
        this.batchRepository = batchRepository;
        this.courseRepository = courseRepository;
        this.securityUtil = securityUtil;
    }

    @Override
    public Exam createExam(Exam exam) {

        exam.setStatus("DRAFT");
        exam.setIsDeleted(false);
        exam.setCreatedBy(securityUtil.getUserId());

        return examRepository.save(exam);
    }

    @Override
    public List<Exam> getAllExams() {
        List<Exam> exams = examRepository.findAll();
        exams.forEach(this::populateLabels);
        return exams;
    }

    private void populateLabels(Exam exam) {
        if (exam == null) return;
        
        // Count questions
        exam.setQuestionCount(examQuestionRepository.countByExamId(exam.getExamId()));
        
        // Fetch labels
        if (exam.getCourseId() != null) {
            courseRepository.findById(exam.getCourseId()).ifPresent(c -> exam.setCourseName(c.getCourseName()));
        }
        if (exam.getBatchId() != null) {
            batchRepository.findById(exam.getBatchId()).ifPresent(b -> exam.setBatchName(b.getBatchName()));
        }
    }

    @Override
    public Exam publishExam(Long examId) {
        Exam exam = getExamById(examId);
        exam.setStatus("PUBLISHED");
        return examRepository.save(exam);
    }

    @Override
    public Exam closeExam(Long examId) {
        Exam exam = getExamById(examId);
        exam.setStatus("CLOSED");
        return examRepository.save(exam);
    }

    @Override
    public Exam getExamById(Long examId) {
        Exam exam = examRepository.findByExamIdAndIsDeletedFalse(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));
        populateLabels(exam);
        return exam;
    }

    // ================= DELETE LOGIC =================

    @Override
    public void softDeleteExam(Long examId) {
        Exam exam = getExamById(examId);
        exam.setIsDeleted(true);
        examRepository.save(exam);
    }

    @Override
    public void restoreExam(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));
        exam.setIsDeleted(false);
        examRepository.save(exam);
    }

    @Override
    public void hardDeleteExam(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));
        examRepository.delete(exam);
    }

    @Override
    public List<Exam> getSoftDeletedExams() {
        return examRepository.findByIsDeletedTrue();
    }
}
