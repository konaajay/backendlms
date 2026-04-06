package com.lms.www.management.service;

import com.lms.www.management.model.ExamNotification;

public interface ExamNotificationService {

    ExamNotification saveNotification(Long examId, ExamNotification notification);

    ExamNotification getNotificationByExamId(Long examId);
}
