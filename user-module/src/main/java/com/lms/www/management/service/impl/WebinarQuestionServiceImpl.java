package com.lms.www.management.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.www.management.enums.QuestionStatus;
import com.lms.www.management.model.Webinar;
import com.lms.www.management.model.WebinarQuestion;
import com.lms.www.management.repository.WebinarQuestionRepository;
import com.lms.www.management.repository.WebinarRepository;
import com.lms.www.management.service.WebinarQuestionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class WebinarQuestionServiceImpl implements WebinarQuestionService {

    private final WebinarQuestionRepository questionRepository;
    private final WebinarRepository webinarRepository;

    @Override
    public WebinarQuestion askQuestion(Long webinarId, Long senderId, String senderName, String question) {

        if (question == null || question.trim().isEmpty()) {
            throw new IllegalArgumentException("Question text must not be empty");
        }

        if (senderName == null || senderName.trim().isEmpty()) {
            throw new IllegalArgumentException("Sender name must not be null");
        }

        if (senderId == null) {
            throw new IllegalArgumentException("Sender ID must not be null");
        }

        Webinar webinar = webinarRepository.findById(webinarId)
                .orElseThrow(() -> new IllegalArgumentException("Webinar does not exist"));

        WebinarQuestion webinarQuestion = WebinarQuestion.builder()
                .webinar(webinar)
                .senderId(senderId)
                .senderName(senderName)
                .question(question)
                .build();

        return questionRepository.save(webinarQuestion);
    }

    @Override
    public WebinarQuestion answerQuestion(Long questionId, String answer) {

        if (answer == null || answer.trim().isEmpty()) {
            throw new IllegalArgumentException("Answer text must not be empty");
        }

        WebinarQuestion webinarQuestion = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found"));

        webinarQuestion.setAnswer(answer);
        webinarQuestion.setStatus(QuestionStatus.ANSWERED);
        webinarQuestion.setAnsweredAt(LocalDateTime.now());

        return questionRepository.save(webinarQuestion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WebinarQuestion> getQuestionsByWebinar(Long webinarId) {

        if (!webinarRepository.existsById(webinarId)) {
            throw new IllegalArgumentException("Webinar does not exist");
        }

        return questionRepository.findByWebinar_WebinarIdOrderByAskedAtAsc(webinarId);
    }
}