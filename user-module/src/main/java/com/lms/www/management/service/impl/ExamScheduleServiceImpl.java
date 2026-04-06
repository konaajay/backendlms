package com.lms.www.management.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.www.common.exception.ResourceNotFoundException;
import com.lms.www.management.model.Batch;
import com.lms.www.management.model.ExamSchedule;
import com.lms.www.management.repository.BatchRepository;
import com.lms.www.management.repository.CourseRepository;
import com.lms.www.management.repository.ExamRepository;
import com.lms.www.management.repository.ExamScheduleRepository;
import com.lms.www.management.service.ExamScheduleService;

@Service
@Transactional
public class ExamScheduleServiceImpl implements ExamScheduleService {

    private final ExamScheduleRepository scheduleRepository;
    private final ExamRepository examRepository;
    private final CourseRepository courseRepository;
    private final BatchRepository batchRepository;

    public ExamScheduleServiceImpl(
            ExamScheduleRepository scheduleRepository,
            ExamRepository examRepository,
            CourseRepository courseRepository,
            BatchRepository batchRepository) {

        this.scheduleRepository = scheduleRepository;
        this.examRepository = examRepository;
        this.courseRepository = courseRepository;
        this.batchRepository = batchRepository;
    }

    @Override
    public ExamSchedule createSchedule(ExamSchedule schedule) {

        // 1️⃣ Validate exam exists and is not deleted
        examRepository.findByExamIdAndIsDeletedFalse(schedule.getExamId())
                .orElseThrow(() -> new IllegalStateException("Invalid examId"));

        // 2️⃣ Validate course exists
        courseRepository.findById(schedule.getCourseId())
                .orElseThrow(() -> new IllegalStateException("Invalid courseId"));

        // 3️⃣ Validate batch exists
        Batch batch = batchRepository.findById(schedule.getBatchId())
                .orElseThrow(() -> new IllegalStateException("Invalid batchId"));

        // 4️⃣ Validate batch belongs to course
        if (!batch.getCourseId().equals(schedule.getCourseId())) {
            throw new IllegalStateException("Batch does not belong to given course");
        }

        // 5️⃣ Validate start and end time
        if (schedule.getStartTime() == null || schedule.getEndTime() == null) {
            throw new IllegalStateException("Start time and end time are required");
        }

        if (schedule.getEndTime().isBefore(schedule.getStartTime())) {
            throw new IllegalStateException("End time must be after start time");
        }

        schedule.setIsActive(true);
        return scheduleRepository.save(schedule);
    }

    @Override
    public ExamSchedule getScheduleById(Long scheduleId) {
        return scheduleRepository.findByScheduleIdAndIsActiveTrue(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam schedule not found"));
    }

    @Override
    public List<ExamSchedule> getSchedulesByExamId(Long examId) {
        return scheduleRepository.findByExamIdAndIsActiveTrue(examId);
    }

    @Override
    public List<ExamSchedule> getSchedulesByCourseId(Long courseId) {
        return scheduleRepository.findByCourseIdAndIsActiveTrue(courseId);
    }

    @Override
    public List<ExamSchedule> getSchedulesByBatchId(Long batchId) {
        return scheduleRepository.findByBatchIdAndIsActiveTrue(batchId);
    }

    @Override
    public void deactivateSchedule(Long scheduleId) {
        ExamSchedule schedule = getScheduleById(scheduleId);
        schedule.setIsActive(false);
        scheduleRepository.save(schedule);
    }
    
    @Override
    public List<ExamSchedule> getDeactivatedSchedules() {
        return scheduleRepository.findByIsActiveFalse();
    }
    
    @Override
    public void restoreSchedule(Long scheduleId) {
        ExamSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found"));

        schedule.setIsActive(true);
        scheduleRepository.save(schedule);
    }
}