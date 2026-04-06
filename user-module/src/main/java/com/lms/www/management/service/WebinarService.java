package com.lms.www.management.service;

import java.util.List;

import com.lms.www.management.enums.WebinarStatus;
import com.lms.www.management.model.Webinar;

public interface WebinarService {

    Webinar createWebinar(Webinar webinar);

    Webinar updateWebinar(Long webinarId, Webinar webinar);

    Webinar getWebinarById(Long webinarId);

    List<Webinar> getAllWebinars();

    List<Webinar> getScheduledWebinars();

    void cancelWebinar(Long webinarId);
    
    void hardDeleteWebinar(Long webinarId);
    
    Webinar changeWebinarStatus(Long webinarId, WebinarStatus status);
}
