package com.lms.www.management.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.www.common.exception.ResourceNotFoundException;
import com.lms.www.management.model.Exam;
import com.lms.www.management.repository.ExamRepository;
import com.lms.www.management.service.ExamService;

@Service
@Transactional
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final com.lms.www.management.util.SecurityUtil securityUtil;

    public ExamServiceImpl(ExamRepository examRepository, com.lms.www.management.util.SecurityUtil securityUtil) {
        this.examRepository = examRepository;
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
        return examRepository.findAll();
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
        return examRepository.findByExamIdAndIsDeletedFalse(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));
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
