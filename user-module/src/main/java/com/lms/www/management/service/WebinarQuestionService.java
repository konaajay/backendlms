package com.lms.www.management.service;

import java.util.List;

import com.lms.www.management.model.WebinarQuestion;

public interface WebinarQuestionService {

    WebinarQuestion askQuestion(Long webinarId, Long senderId, String senderName, String question);

    WebinarQuestion answerQuestion(Long questionId, String answer);

    List<WebinarQuestion> getQuestionsByWebinar(Long webinarId);

}