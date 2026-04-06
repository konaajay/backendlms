package com.lms.www.management.dashboard.dto;

import java.io.Serializable;

public class ProgressSummaryDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private int totalCourses;
    private int completedCourses;
    private int activeCourses;
    private double overallProgressPercentage;

    public ProgressSummaryDTO() {}
    public ProgressSummaryDTO(int totalCourses, int completedCourses, int activeCourses, double overallProgressPercentage) {
        this.totalCourses = totalCourses;
        this.completedCourses = completedCourses;
        this.activeCourses = activeCourses;
        this.overallProgressPercentage = overallProgressPercentage;
    }

    public int getTotalCourses() { return totalCourses; }
    public void setTotalCourses(int totalCourses) { this.totalCourses = totalCourses; }
    public int getCompletedCourses() { return completedCourses; }
    public void setCompletedCourses(int completedCourses) { this.completedCourses = completedCourses; }
    public int getActiveCourses() { return activeCourses; }
    public void setActiveCourses(int activeCourses) { this.activeCourses = activeCourses; }
    public double getOverallProgressPercentage() { return overallProgressPercentage; }
    public void setOverallProgressPercentage(double percentage) { this.overallProgressPercentage = percentage; }

    public static ProgressSummaryDTOBuilder builder() { return new ProgressSummaryDTOBuilder(); }
    public static class ProgressSummaryDTOBuilder {
        private int totalCourses;
        private int completedCourses;
        private int activeCourses;
        private double overallProgressPercentage;

        public ProgressSummaryDTOBuilder totalCourses(int total) { this.totalCourses = total; return this; }
        public ProgressSummaryDTOBuilder completedCourses(int completed) { this.completedCourses = completed; return this; }
        public ProgressSummaryDTOBuilder activeCourses(int active) { this.activeCourses = active; return this; }
        public ProgressSummaryDTOBuilder overallProgressPercentage(double percentage) { this.overallProgressPercentage = percentage; return this; }
        public ProgressSummaryDTO build() {
            return new ProgressSummaryDTO(totalCourses, completedCourses, activeCourses, overallProgressPercentage);
        }
    }
}