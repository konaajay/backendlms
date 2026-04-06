package com.lms.www.management.dashboard.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.lms.www.management.dashboard.dto.CourseProgressDTO;
import com.lms.www.management.dashboard.dto.DashboardCourseDataDTO;
import com.lms.www.management.dashboard.dto.TopicContentDTO;
import com.lms.www.management.dashboard.dto.TopicDTO;
import com.lms.www.management.dashboard.service.DashboardCourseService;
import com.lms.www.management.model.Course;
import com.lms.www.management.model.Session;
import com.lms.www.management.model.StudentBatch;
import com.lms.www.management.model.StudentVideoProgress;
import com.lms.www.management.model.Topic;
import com.lms.www.management.repository.CourseRepository;
import com.lms.www.management.repository.SessionRepository;
import com.lms.www.management.repository.StudentBatchRepository;
import com.lms.www.management.repository.StudentVideoProgressRepository;
import com.lms.www.management.repository.TopicContentRepository;
import com.lms.www.management.repository.TopicRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardCourseServiceImpl implements DashboardCourseService {

    private final StudentBatchRepository studentBatchRepository;
    private final TopicRepository topicRepository;
    private final SessionRepository sessionRepository;
    private final StudentVideoProgressRepository videoProgressRepository;
    private final CourseRepository courseRepository;
    private final TopicContentRepository topicContentRepository;

    @Override
    public List<DashboardCourseDataDTO> getCoursesForStudent(Long studentId) {

        List<StudentBatch> userBatches = studentBatchRepository.findByStudentId(studentId);

        List<Course> enrolledCourses = userBatches.stream()
                .map(sb -> courseRepository.findById(sb.getCourseId()).orElse(null))
                .filter(course -> course != null)
                .distinct()
                .collect(Collectors.toList());

        List<DashboardCourseDataDTO> resultList = new ArrayList<>();

        for (Course course : enrolledCourses) {

            List<Topic> topics = topicRepository.findByCourseCourseId(course.getCourseId());
            List<TopicDTO> topicDTOs = new ArrayList<>();

            int totalSessions = 0;
            int completedSessions = 0;

            // 🔥 batches for this course
            List<StudentBatch> courseBatches = userBatches.stream()
                    .filter(sb -> sb.getCourseId().equals(course.getCourseId()))
                    .collect(Collectors.toList());

            // 🔥 collect sessions (for progress only, NOT for topics)
            List<Session> allSessions = courseBatches.stream()
                    .flatMap(sb -> sessionRepository.findByBatchId(sb.getBatch().getBatchId()).stream())
                    .collect(Collectors.toList());

            for (Session session : allSessions) {

                totalSessions++;

                List<StudentVideoProgress> progressList =
                        videoProgressRepository.findByUserIdAndSessionId(studentId, session.getSessionId());

                if (!progressList.isEmpty()) {
                    StudentVideoProgress prog = progressList.get(0);

                    double videoPct = prog.getPercentageWatched() != null
                            ? prog.getPercentageWatched()
                            : 0.0;

                    if (videoPct >= 95.0) {
                        completedSessions++;
                    }
                }
            }

            // ✅ topics WITHOUT sessions
            for (Topic topic : topics) {

                // 🔥 FETCH TOPIC CONTENTS
                List<TopicContentDTO> contentDTOs =
                        topicContentRepository.findByTopicTopicId(topic.getTopicId())
                                .stream()
                                .map(c -> TopicContentDTO.builder()
                                        .contentId(c.getContentId())
                                        .contentType(c.getContentType())
                                        .contentSource(c.getContentSource())
                                        .contentTitle(c.getContentTitle())
                                        .contentDescription(c.getContentDescription())
                                        .fileUrl(c.getFileUrl())
                                        .contentOrder(c.getContentOrder())
                                        .build())
                                .collect(Collectors.toList());

                topicDTOs.add(
                        TopicDTO.builder()
                                .topicId(topic.getTopicId())
                                .topicName(topic.getTopicName())
                                .topicOrder(topic.getSequenceOrder())
                                .contents(contentDTOs) // ✅ ADD THIS
                                .build()
                );
            }

            double courseProgressPercentage =
                    totalSessions > 0
                            ? ((double) completedSessions / totalSessions) * 100
                            : 0.0;

            boolean isCourseComplete =
                    totalSessions > 0 && totalSessions == completedSessions;

            CourseProgressDTO courseDTO =
                    CourseProgressDTO.builder()
                            .courseId(course.getCourseId())
                            .courseName(course.getCourseName())
                            .courseDescription(course.getDescription())
                            .progressPercentage(courseProgressPercentage)
                            .topics(topicDTOs)
                            .build();

            resultList.add(
                    new DashboardCourseDataDTO(
                            courseDTO,
                            isCourseComplete,
                            courseProgressPercentage < 100.0 && courseProgressPercentage > 0
                    )
            );
        }

        return resultList;
    }
}