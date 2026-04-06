package com.lms.www.management.dashboard.dto;

import java.io.Serializable;

public class StudentInfoDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long studentId;
    private String studentName;
    private String email;
    private String phone;
    private String status;

    public StudentInfoDTO() {}
    public StudentInfoDTO(Long studentId, String studentName, String email, String phone, String status) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.email = email;
        this.phone = phone;
        this.status = status;
    }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String name) { this.studentName = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public static StudentInfoDTOBuilder builder() { return new StudentInfoDTOBuilder(); }
    public static class StudentInfoDTOBuilder {
        private Long studentId;
        private String studentName;
        private String email;
        private String phone;
        private String status;

        public StudentInfoDTOBuilder studentId(Long id) { this.studentId = id; return this; }
        public StudentInfoDTOBuilder studentName(String name) { this.studentName = name; return this; }
        public StudentInfoDTOBuilder email(String email) { this.email = email; return this; }
        public StudentInfoDTOBuilder phone(String phone) { this.phone = phone; return this; }
        public StudentInfoDTOBuilder status(String status) { this.status = status; return this; }
        public StudentInfoDTO build() {
            return new StudentInfoDTO(studentId, studentName, email, phone, status);
        }
    }
}