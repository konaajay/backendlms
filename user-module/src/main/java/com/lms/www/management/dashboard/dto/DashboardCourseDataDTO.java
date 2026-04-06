package com.lms.www.management.dashboard.dto;

public class DashboardCourseDataDTO {
    private CourseProgressDTO courseProgress;
    private boolean completed;
    private boolean active;

    public DashboardCourseDataDTO() {}
    public DashboardCourseDataDTO(CourseProgressDTO courseProgress, boolean completed, boolean active) {
        this.courseProgress = courseProgress;
        this.completed = completed;
        this.active = active;
    }

    public CourseProgressDTO getCourseProgress() { return courseProgress; }
    public void setCourseProgress(CourseProgressDTO courseProgress) { this.courseProgress = courseProgress; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public static DashboardCourseDataDTOBuilder builder() { return new DashboardCourseDataDTOBuilder(); }
    public static class DashboardCourseDataDTOBuilder {
        private CourseProgressDTO courseProgress;
        private boolean completed;
        private boolean active;

        public DashboardCourseDataDTOBuilder courseProgress(CourseProgressDTO progress) { this.courseProgress = progress; return this; }
        public DashboardCourseDataDTOBuilder completed(boolean completed) { this.completed = completed; return this; }
        public DashboardCourseDataDTOBuilder active(boolean active) { this.active = active; return this; }
        public DashboardCourseDataDTO build() {
            return new DashboardCourseDataDTO(courseProgress, completed, active);
        }
    }
}