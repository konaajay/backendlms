package com.lms.www.management.dashboard.dto;

import java.util.List;

public class CourseProgressDTO {
    private Long courseId;
    private String courseName;
    private String courseDescription;
    private double progressPercentage;
    private List<TopicDTO> topics;

    public CourseProgressDTO() {}
    public CourseProgressDTO(Long courseId, String courseName, String courseDescription, double progressPercentage, List<TopicDTO> topics) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.courseDescription = courseDescription;
        this.progressPercentage = progressPercentage;
        this.topics = topics;
    }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public String getCourseDescription() { return courseDescription; }
    public void setCourseDescription(String courseDescription) { this.courseDescription = courseDescription; }
    public double getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(double progressPercentage) { this.progressPercentage = progressPercentage; }
    public List<TopicDTO> getTopics() { return topics; }
    public void setTopics(List<TopicDTO> topics) { this.topics = topics; }

    public static CourseProgressDTOBuilder builder() { return new CourseProgressDTOBuilder(); }
    public static class CourseProgressDTOBuilder {
        private Long courseId;
        private String courseName;
        private String courseDescription;
        private double progressPercentage;
        private List<TopicDTO> topics;

        public CourseProgressDTOBuilder courseId(Long id) { this.courseId = id; return this; }
        public CourseProgressDTOBuilder courseName(String name) { this.courseName = name; return this; }
        public CourseProgressDTOBuilder courseDescription(String desc) { this.courseDescription = desc; return this; }
        public CourseProgressDTOBuilder progressPercentage(double percentage) { this.progressPercentage = percentage; return this; }
        public CourseProgressDTOBuilder topics(List<TopicDTO> topics) { this.topics = topics; return this; }
        public CourseProgressDTO build() {
            return new CourseProgressDTO(courseId, courseName, courseDescription, progressPercentage, topics);
        }
    }
}