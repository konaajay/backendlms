package com.lms.www.management.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.www.management.enums.WebinarPollStatus;
import com.lms.www.management.model.WebinarPoll;
import com.lms.www.management.model.WebinarPollResponse;
import com.lms.www.management.repository.WebinarPollRepository;
import com.lms.www.management.repository.WebinarPollResponseRepository;
import com.lms.www.management.service.WebinarPollResponseService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WebinarPollResponseServiceImpl implements WebinarPollResponseService {

    private final WebinarPollRepository pollRepository;
    private final WebinarPollResponseRepository responseRepository;

    @Override
    @Transactional
    public WebinarPollResponse submitVote(Long pollId, Long userId, Long participantId, String selectedOption) {

        // Validate participant
        if (userId == null && participantId == null) {
            throw new IllegalArgumentException("Either userId or participantId must be provided");
        }

        if (userId != null && participantId != null) {
            throw new IllegalArgumentException("Only one of userId or participantId should be provided");
        }

        // Fetch poll
        WebinarPoll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new IllegalArgumentException("Poll not found"));

        // Check poll status
        if (!WebinarPollStatus.ACTIVE.equals(poll.getStatus())) {
            throw new IllegalStateException("Poll is not active");
        }

        // Validate option
        if (!poll.getOptions().contains(selectedOption)) {
            throw new IllegalArgumentException("Invalid option selected");
        }

        // Prevent duplicate vote (internal user)
        if (userId != null && responseRepository.existsByPoll_PollIdAndUserId(pollId, userId)) {
            throw new IllegalStateException("User has already voted");
        }

        // Prevent duplicate vote (external participant)
        if (participantId != null && responseRepository.existsByPoll_PollIdAndParticipantId(pollId, participantId)) {
            throw new IllegalStateException("Participant has already voted");
        }

        // Save response
        WebinarPollResponse response = WebinarPollResponse.builder()
                .poll(poll)
                .userId(userId)
                .participantId(participantId)
                .selectedOption(selectedOption)
                .build();

        return responseRepository.save(response);
    }

    @Override
    public List<WebinarPollResponse> getPollResponses(Long pollId) {
        return responseRepository.findByPoll_PollId(pollId);
    }

    @Override
    public Map<String, Long> getPollResultStatistics(Long pollId) {
        List<WebinarPollResponse> responses = responseRepository.findByPoll_PollId(pollId);

        return responses.stream()
                .collect(Collectors.groupingBy(
                        WebinarPollResponse::getSelectedOption,
                        Collectors.counting()
                ));
    }
}