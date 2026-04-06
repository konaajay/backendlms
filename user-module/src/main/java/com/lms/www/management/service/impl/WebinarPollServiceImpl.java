package com.lms.www.management.service.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.www.management.enums.WebinarPollStatus;
import com.lms.www.management.model.Webinar;
import com.lms.www.management.model.WebinarPoll;
import com.lms.www.management.model.WebinarPollResponse;
import com.lms.www.management.repository.WebinarPollRepository;
import com.lms.www.management.repository.WebinarPollResponseRepository;
import com.lms.www.management.repository.WebinarRepository;
import com.lms.www.management.service.WebinarPollService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class WebinarPollServiceImpl implements WebinarPollService {

    private final WebinarPollRepository pollRepository;
    private final WebinarPollResponseRepository responseRepository;
    private final WebinarRepository webinarRepository;

    @Override
    public WebinarPoll createPoll(Long webinarId, String question, List<String> options, Long createdBy) {

        if (question == null || question.trim().isEmpty()) {
            throw new IllegalArgumentException("Poll question must not be empty");
        }

        if (options == null || options.size() < 2) {
            throw new IllegalArgumentException("Poll must have at least two options");
        }

        Webinar webinar = webinarRepository.findById(webinarId)
                .orElseThrow(() -> new IllegalArgumentException("Webinar does not exist"));

        WebinarPoll poll = WebinarPoll.builder()
                .webinar(webinar)
                .question(question)
                .options(options)
                .createdBy(createdBy)
                .build();

        return pollRepository.save(poll);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WebinarPoll> getPollsByWebinar(Long webinarId) {

        if (!webinarRepository.existsById(webinarId)) {
            throw new IllegalArgumentException("Webinar does not exist");
        }

        return pollRepository.findByWebinar_WebinarIdOrderByCreatedAtAsc(webinarId);
    }

    @Override
    public WebinarPoll updatePollStatus(Long pollId, WebinarPollStatus status) {

        WebinarPoll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new IllegalArgumentException("Poll not found"));

        poll.setStatus(status);

        return pollRepository.save(poll);
    }

    @Override
    public void deletePoll(Long pollId) {

        if (!pollRepository.existsById(pollId)) {
            throw new IllegalArgumentException("Poll not found");
        }

        pollRepository.deleteById(pollId);
    }

    @Override
    public WebinarPollResponse submitResponse(Long pollId, Long userId, String selectedOption) {

        if (userId == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }

        WebinarPoll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new IllegalArgumentException("Poll not found"));

        if (poll.getStatus() != WebinarPollStatus.ACTIVE) {
            throw new IllegalStateException("Poll is not currently active");
        }

        if (!poll.getOptions().contains(selectedOption)) {
            throw new IllegalArgumentException("Selected option is not valid for this poll");
        }

        if (responseRepository.existsByPoll_PollIdAndUserId(pollId, userId)) {
            throw new IllegalStateException("User has already responded to this poll");
        }

        WebinarPollResponse response = WebinarPollResponse.builder()
                .poll(poll)
                .userId(userId)
                .selectedOption(selectedOption)
                .build();

        return responseRepository.save(response);
    }

    @Override
    @Transactional(readOnly = true)
    public LinkedHashMap<String, Long> getPollResults(Long pollId) {

        WebinarPoll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new IllegalArgumentException("Poll not found"));

        List<WebinarPollResponse> responses = responseRepository.findByPoll_PollId(pollId);

        LinkedHashMap<String, Long> results = poll.getOptions()
                .stream()
                .collect(Collectors.toMap(
                        option -> option,
                        option -> 0L,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));

        responses.forEach(response -> {
            String option = response.getSelectedOption();
            results.put(option, results.get(option) + 1);
        });

        return results;
    }
}