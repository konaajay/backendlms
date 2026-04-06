package com.lms.www.management.dashboard.dto;

public class AttendanceSummaryDTO {
    private int totalSessions;
    private int attendedSessions;
    private int missedSessions;
    private double attendancePercentage;

    public AttendanceSummaryDTO() {}
    public AttendanceSummaryDTO(int totalSessions, int attendedSessions, int missedSessions, double attendancePercentage) {
        this.totalSessions = totalSessions;
        this.attendedSessions = attendedSessions;
        this.missedSessions = missedSessions;
        this.attendancePercentage = attendancePercentage;
    }

    public int getTotalSessions() { return totalSessions; }
    public void setTotalSessions(int totalSessions) { this.totalSessions = totalSessions; }
    public int getAttendedSessions() { return attendedSessions; }
    public void setAttendedSessions(int attendedSessions) { this.attendedSessions = attendedSessions; }
    public int getMissedSessions() { return missedSessions; }
    public void setMissedSessions(int missedSessions) { this.missedSessions = missedSessions; }
    public double getAttendancePercentage() { return attendancePercentage; }
    public void setAttendancePercentage(double attendancePercentage) { this.attendancePercentage = attendancePercentage; }

    public static AttendanceSummaryDTOBuilder builder() { return new AttendanceSummaryDTOBuilder(); }
    public static class AttendanceSummaryDTOBuilder {
        private int totalSessions;
        private int attendedSessions;
        private int missedSessions;
        private double attendancePercentage;

        public AttendanceSummaryDTOBuilder totalSessions(int total) { this.totalSessions = total; return this; }
        public AttendanceSummaryDTOBuilder attendedSessions(int attended) { this.attendedSessions = attended; return this; }
        public AttendanceSummaryDTOBuilder missedSessions(int missed) { this.missedSessions = missed; return this; }
        public AttendanceSummaryDTOBuilder attendancePercentage(double percentage) { this.attendancePercentage = percentage; return this; }
        public AttendanceSummaryDTO build() {
            return new AttendanceSummaryDTO(totalSessions, attendedSessions, missedSessions, attendancePercentage);
        }
    }
}