package com.lms.www.management.service;

import java.util.LinkedHashMap;
import java.util.List;

import com.lms.www.management.enums.WebinarPollStatus;
import com.lms.www.management.model.WebinarPoll;
import com.lms.www.management.model.WebinarPollResponse;

public interface WebinarPollService {

    WebinarPoll createPoll(Long webinarId, String question, List<String> options, Long createdBy);

    List<WebinarPoll> getPollsByWebinar(Long webinarId);

    WebinarPoll updatePollStatus(Long pollId, WebinarPollStatus status);

    void deletePoll(Long pollId);

    WebinarPollResponse submitResponse(Long pollId, Long userId, String selectedOption);

    LinkedHashMap<String, Long> getPollResults(Long pollId);

}