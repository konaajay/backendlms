package com.lms.www.management.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.www.management.exception.ResourceNotFoundException;
import com.lms.www.management.model.Topic;
import com.lms.www.management.repository.CourseRepository;
import com.lms.www.management.service.TopicService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
public class TopicController {

    private final TopicService topicService;
    private final CourseRepository courseRepository;

    // ===============================
    // CREATE TOPIC
    // ===============================
    @PostMapping("/course/{courseId}")
    @PreAuthorize("hasAuthority('TOPIC_CREATE')")
    public ResponseEntity<Topic> createTopic(
            @PathVariable Long courseId,
            @RequestBody Topic topic) {

        Topic createdTopic = topicService.createTopic(courseId, topic);
        return new ResponseEntity<>(createdTopic, HttpStatus.CREATED);
    }

    // ===============================
    // GET ALL TOPICS
    // ===============================
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Topic>> getAllTopics() {

        List<Topic> topics = topicService.getAllTopics();
        return ResponseEntity.ok(topics);
    }

    // ===============================
    // GET TOPIC BY ID
    // ===============================
    @GetMapping("/{topicId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Topic> getTopicById(@PathVariable Long topicId) {

        Topic topic = topicService.getTopicById(topicId);
        return ResponseEntity.ok(topic);
    }

    // ===============================
    // GET TOPICS BY COURSE
    // ===============================
    @GetMapping("/course/{courseId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Topic>> getTopicsByCourseId(
            @PathVariable Long courseId) {

        courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Course not found with id: " + courseId)
                );

        List<Topic> topics = topicService.getTopicsByCourseId(courseId);
        return ResponseEntity.ok(topics);
    }

    // ===============================
    // UPDATE TOPIC
    // ===============================
    @PutMapping("/{topicId}")
    @PreAuthorize("hasAuthority('TOPIC_UPDATE')")
    public ResponseEntity<Topic> updateTopic(
            @PathVariable Long topicId,
            @RequestBody Topic topic) {

        Topic updatedTopic = topicService.updateTopic(topicId, topic);
        return ResponseEntity.ok(updatedTopic);
    }

    // ===============================
    // DELETE TOPIC
    // ===============================
    @DeleteMapping("/{topicId}")
    @PreAuthorize("hasAuthority('TOPIC_DELETE')")
    public ResponseEntity<Void> deleteTopic(@PathVariable Long topicId) {

        topicService.deleteTopic(topicId);
        return ResponseEntity.noContent().build();
    }
}