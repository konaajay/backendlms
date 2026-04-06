package com.lms.www.management.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lms.www.common.exception.ResourceNotFoundException;
import com.lms.www.management.exception.UnauthorizedAccessException;
import com.lms.www.management.model.Course;
import com.lms.www.management.model.Topic;
import com.lms.www.management.model.TopicContent;
import com.lms.www.management.repository.CourseRepository;
import com.lms.www.management.repository.TopicContentRepository;
import com.lms.www.management.repository.TopicRepository;
import com.lms.www.management.service.CourseService;
import com.lms.www.management.util.FileUploadUtil;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin
public class CourseController {

    private final CourseService courseService;
    private final CourseRepository courseRepository;
    private final TopicRepository topicRepository;
    private final TopicContentRepository topicContentRepository;

    public CourseController(
            CourseService courseService,
            CourseRepository courseRepository,
            TopicRepository topicRepository,
            TopicContentRepository topicContentRepository
    ) {
        this.courseService = courseService;
        this.courseRepository = courseRepository;
        this.topicRepository = topicRepository;
        this.topicContentRepository = topicContentRepository;
    }

    // ===============================
    // CREATE COURSE
    // ===============================
    @PostMapping
    @PreAuthorize("hasAuthority('COURSE_CREATE')")
    public Course createCourse(@RequestBody Course course) {
        return courseService.createCourse(course);
    }

    // ===============================
    // UPDATE COURSE
    // ===============================
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('COURSE_UPDATE')")
    public Course updateCourse(
            @PathVariable Long id,
            @RequestBody Course course) {

        return courseService.updateCourse(id, course);
    }

    // ===============================
    // UPDATE COURSE IMAGE
    // ===============================
    @PutMapping("/{id}/image")
    @PreAuthorize("hasAuthority('COURSE_UPDATE')")
    public Course updateCourseImage(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile image
    ) throws IOException {

        String imageUrl = FileUploadUtil.saveCourseImage(image);

        Course incoming = new Course();
        incoming.setCourseImageUrl(imageUrl);

        return courseService.updateCourse(id, incoming);
    }

    // ===============================
    // GET ALL COURSES
    // ===============================
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    // ===============================
    // GET COURSE BY ID
    // ===============================
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public Course getCourse(@PathVariable Long id) {
        return courseService.getCourseById(id);
    }

    // ===============================
    // DELETE COURSE (SOFT)
    // ===============================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('COURSE_DELETE')")
    public void deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
    }

    // ===============================
    // DELETE COURSE (HARD)
    // ===============================
    @DeleteMapping("/{id}/hard")
    @PreAuthorize("hasAuthority('COURSE_HARD_DELETE')")
    public void hardDeleteCourse(@PathVariable Long id) {
        courseService.hardDeleteCourse(id);
    }

    // ===============================
    // 🔗 SHARE COURSE (PUBLIC API)
    // ===============================
    @GetMapping("/share/{shareCode}")
    public Course shareCourse(@PathVariable String shareCode) {

        Course course = courseRepository.findByShareCode(shareCode)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Invalid share link")
                );

        if (!"ACTIVE".equalsIgnoreCase(course.getStatus())) {
            throw new UnauthorizedAccessException("Course is not active");
        }

        if (Boolean.FALSE.equals(course.getShareEnabled())) {
            throw new UnauthorizedAccessException("Sharing disabled");
        }

        // ❌ REMOVED content access check

        // 🔥 LOAD TOPICS
        List<Topic> topics =
                topicRepository.findByCourseCourseId(course.getCourseId());

        // 🔥 LOAD CONTENTS
        for (Topic topic : topics) {
            List<TopicContent> contents =
                    topicContentRepository.findByTopicTopicId(topic.getTopicId());
            topic.setContents(contents);
        }

        course.setTopics(topics);

        return course;
    }
}