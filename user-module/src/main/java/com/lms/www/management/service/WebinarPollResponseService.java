package com.lms.www.management.service;

import java.util.List;
import java.util.Map;

import com.lms.www.management.model.WebinarPollResponse;

public interface WebinarPollResponseService {

    // Submit a vote for a poll
    WebinarPollResponse submitVote(Long pollId, Long userId, Long participantId, String selectedOption);

    // Get all responses for a poll
    List<WebinarPollResponse> getPollResponses(Long pollId);

    // Get aggregated poll results (A,B,C,D counts)
    Map<String, Long> getPollResultStatistics(Long pollId);
}