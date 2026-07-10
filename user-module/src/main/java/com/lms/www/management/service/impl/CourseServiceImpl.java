package com.lms.www.management.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.www.common.exception.ResourceNotFoundException;
import com.lms.www.management.model.Course;
import com.lms.www.management.model.Topic;
import com.lms.www.management.model.TopicContent;
import com.lms.www.management.repository.CourseRepository;
import com.lms.www.management.repository.TopicContentRepository;
import com.lms.www.management.repository.TopicRepository;
import com.lms.www.management.service.CourseService;
import com.lms.www.management.model.CourseBookmark;
import com.lms.www.management.repository.CourseBookmarkRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import com.lms.www.config.CustomUserDetails;
import java.time.LocalDateTime;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final TopicRepository topicRepository;
    private final TopicContentRepository topicContentRepository;
    private final CourseBookmarkRepository courseBookmarkRepository;

    public CourseServiceImpl(
            CourseRepository courseRepository,
            TopicRepository topicRepository,
            TopicContentRepository topicContentRepository,
            CourseBookmarkRepository courseBookmarkRepository
    ) {
        this.courseRepository = courseRepository;
        this.topicRepository = topicRepository;
        this.topicContentRepository = topicContentRepository;
        this.courseBookmarkRepository = courseBookmarkRepository;
    }

    // ===============================
    // CREATE COURSE
    // ===============================
    @Override
    public Course createCourse(Course course) {

        course.setShareCode("SHR-" + UUID.randomUUID().toString().substring(0, 8));
        course.setShareEnabled(true);

        applyValidityRule(course);

        Course saved = courseRepository.save(course);
        attachShareLink(saved);
        return saved;
    }

    // ===============================
    // UPDATE COURSE (PUT AS PATCH)
    // ===============================
    @Override
    public Course updateCourse(Long courseId, Course incoming) {

        Course existing = courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Course not found with id: " + courseId
                        )
                );

        if (incoming.getCourseName() != null)
            existing.setCourseName(incoming.getCourseName());

        if (incoming.getDescription() != null)
            existing.setDescription(incoming.getDescription());

        if (incoming.getDuration() != null)
            existing.setDuration(incoming.getDuration());

        if (incoming.getToolsCovered() != null)
            existing.setToolsCovered(incoming.getToolsCovered());

        if (incoming.getCourseFee() != null)
            existing.setCourseFee(incoming.getCourseFee());

        if (incoming.getCertificateProvided() != null)
            existing.setCertificateProvided(incoming.getCertificateProvided());

        if (incoming.getStatus() != null)
            existing.setStatus(incoming.getStatus());

        if (incoming.getShowValidity() != null)
            existing.setShowValidity(incoming.getShowValidity());

        if (incoming.getValidityInDays() != null)
            existing.setValidityInDays(incoming.getValidityInDays());

        if (incoming.getAllowOfflineMobile() != null)
            existing.setAllowOfflineMobile(incoming.getAllowOfflineMobile());

        if (incoming.getAllowBookmark() != null)
            existing.setAllowBookmark(incoming.getAllowBookmark());

        // ❌ REMOVED enableContentAccess update block

        if (incoming.getShareEnabled() != null)
            existing.setShareEnabled(incoming.getShareEnabled());

        if (incoming.getCourseImageUrl() != null)
            existing.setCourseImageUrl(incoming.getCourseImageUrl());

        if (Boolean.TRUE.equals(existing.getShareEnabled())
                && existing.getShareCode() == null) {
            existing.setShareCode(
                    "SHR-" + UUID.randomUUID().toString().substring(0, 8)
            );
        }

        applyValidityRule(existing);

        Course saved = courseRepository.save(existing);
        attachShareLink(saved);
        return saved;
    }

    // ===============================
    // GET COURSE BY ID
    // ===============================
    @Override
    public Course getCourseById(Long courseId) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Course not found with id: " + courseId
                        )
                );

        loadCurriculum(course);
        attachShareLink(course);
        populateBookmark(course);
        return course;
    }

    // ===============================
    // GET ALL COURSES
    // ===============================
    @Override
    public List<Course> getAllCourses() {

        List<Course> courses = courseRepository.findAll();
        courses.forEach(c -> {
            loadCurriculum(c);
            attachShareLink(c);
        });
        populateBookmarks(courses);
        return courses;
    }

    private void loadCurriculum(Course course) {
        List<Topic> topics = topicRepository.findByCourseCourseId(course.getCourseId());
        for (Topic topic : topics) {
            List<TopicContent> contents = topicContentRepository.findByTopicTopicId(topic.getTopicId());
            topic.setContents(contents);
        }
        course.setTopics(topics);
    }

    // ===============================
    // DELETE COURSE (SOFT DELETE)
    // ===============================
    @Override
    public void deleteCourse(Long courseId) {

        Course course = getCourseById(courseId);
        course.setStatus("INACTIVE");
        courseRepository.save(course);
    }

    // ===============================
    // DELETE COURSE (HARD DELETE)
    // ===============================
    @Override
    public void hardDeleteCourse(Long courseId) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Course not found with id: " + courseId
                        )
                );

        topicContentRepository.deleteByCourseId(courseId);
        topicRepository.deleteByCourseId(courseId);
        courseRepository.delete(course);
    }

    // ===============================
    // HELPERS
    // ===============================
    private void applyValidityRule(Course course) {
        if (Boolean.FALSE.equals(course.getShowValidity())) {
            course.setValidityInDays(null);
        }
    }

    private void attachShareLink(Course course) {
        if (Boolean.TRUE.equals(course.getShareEnabled())
                && course.getShareCode() != null) {

            course.setShareLink(
                    "https://yourapp.com/share/" + course.getShareCode()
            );
            course.setShareLink(null);
        }
    }

    private void populateBookmarks(List<Course> courses) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
            Long userId = userDetails.getId();
            List<CourseBookmark> userBookmarks = courseBookmarkRepository.findByUserId(userId);
            List<Long> bookmarkedCourseIds = userBookmarks.stream()
                .map(CourseBookmark::getCourseId)
                .toList();

            for (Course c : courses) {
                c.setIsBookmarked(bookmarkedCourseIds.contains(c.getCourseId()));
            }
        }
    }

    private void populateBookmark(Course course) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
            Long userId = userDetails.getId();
            course.setIsBookmarked(courseBookmarkRepository.findByCourseIdAndUserId(course.getCourseId(), userId).isPresent());
        }
    }

    @Override
    public boolean toggleBookmark(Long courseId, Long userId) {
        java.util.Optional<CourseBookmark> existing = courseBookmarkRepository.findByCourseIdAndUserId(courseId, userId);
        if (existing.isPresent()) {
            courseBookmarkRepository.deleteByCourseIdAndUserId(courseId, userId);
            return false;
        } else {
            CourseBookmark newBookmark = CourseBookmark.builder()
                .courseId(courseId)
                .userId(userId)
                .createdAt(LocalDateTime.now())
                .build();
            courseBookmarkRepository.save(newBookmark);
            return true;
        }
    }
}