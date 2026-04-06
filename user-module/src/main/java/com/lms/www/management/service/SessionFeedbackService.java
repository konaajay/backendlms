package com.lms.www.management.service;

import com.lms.www.management.model.SessionFeedback;

public interface SessionFeedbackService {

    SessionFeedback submitFeedback(Long sessionId, Long fromUserId, Long toUserId, Integer rating, String comment);

    Double getAverageRatingForUser(Long toUserId);

    Double getTrainerAverageFromStudents(Long trainerId);
}