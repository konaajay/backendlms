package com.lms.www.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.www.management.model.WebinarPollResponse;

@Repository
public interface WebinarPollResponseRepository extends JpaRepository<WebinarPollResponse, Long> {

    // Get all responses for a poll
    List<WebinarPollResponse> findByPoll_PollId(Long pollId);

    // Check duplicate vote for internal user
    boolean existsByPoll_PollIdAndUserId(Long pollId, Long userId);

    // Check duplicate vote for external participant
    boolean existsByPoll_PollIdAndParticipantId(Long pollId, Long participantId);

    // Useful for poll result statistics
    List<WebinarPollResponse> findByPoll_PollIdOrderBySelectedOption(Long pollId);
}