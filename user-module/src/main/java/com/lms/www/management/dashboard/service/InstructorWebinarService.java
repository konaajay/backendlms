package com.lms.www.management.dashboard.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.lms.www.management.model.Webinar;
import com.lms.www.management.model.WebinarAttendance;
import com.lms.www.management.model.WebinarQuestion;
import com.lms.www.management.model.WebinarRecording;

public interface InstructorWebinarService {

    Page<Webinar> getInstructorWebinars(Long instructorId, Pageable pageable);

    Webinar createWebinar(Long instructorId, Webinar webinar);

    List<WebinarAttendance> getAttendeesWithLazyLoadFix(Long instructorId, Long webinarId);

    List<WebinarQuestion> getQuestionsWithLazyLoadFix(Long instructorId, Long webinarId);

    Webinar updateWebinar(Long instructorId, Long webinarId, Webinar webinar);

    void cancelWebinar(Long instructorId, Long webinarId);

    WebinarQuestion answerQuestion(Long instructorId, Long webinarId, Long questionId, String answer);

    WebinarRecording uploadRecording(Long instructorId, Long webinarId, MultipartFile file);

    List<Webinar> getScheduledWebinars(Long instructorId);
}