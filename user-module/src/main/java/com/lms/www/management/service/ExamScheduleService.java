package com.lms.www.management.service;

import java.util.List;

import com.lms.www.management.model.ExamSchedule;

public interface ExamScheduleService {

    ExamSchedule createSchedule(ExamSchedule schedule);

    ExamSchedule getScheduleById(Long scheduleId);

    List<ExamSchedule> getSchedulesByExamId(Long examId);

    List<ExamSchedule> getSchedulesByCourseId(Long courseId);

    List<ExamSchedule> getSchedulesByBatchId(Long batchId);

    void deactivateSchedule(Long scheduleId);
    
    List<ExamSchedule> getDeactivatedSchedules();
    
    void restoreSchedule(Long scheduleId);
}
