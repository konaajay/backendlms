package com.lms.www.campus.Hostel;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@Table(name = "hostel_attendance")
@JsonIgnoreProperties(ignoreUnknown = true)
public class HostelAttendance 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_id")
    private Long attendanceId;

    // ---------------- Student Info  ----------------
	
    @Column(name = "student_id", nullable = true)
    private Long studentId;

    @Column(name = "student_name", nullable = false)
    private String studentName;

    // ---------------- Room Info ----------------
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "room_id", nullable =false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private HostelRoom room;
    
    @Column(name = "room_number")
    private String roomNumber;

    // ---------------- Attendance ----------------
    
    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceStatus status;
    public enum AttendanceStatus 
    {
        PRESENT,
        ABSENT
    }

    // ---------------- Audit ----------------
    @Column(name = "marked_at", nullable = false)
    private LocalDateTime markedAt;

    // Manual Getters and Setters
    public Long getAttendanceId() { return attendanceId; }
    public void setAttendanceId(Long attendanceId) { this.attendanceId = attendanceId; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public HostelRoom getRoom() { return room; }
    public void setRoom(HostelRoom room) { this.room = room; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public LocalDate getAttendanceDate() { return attendanceDate; }
    public void setAttendanceDate(LocalDate attendanceDate) { this.attendanceDate = attendanceDate; }

    public AttendanceStatus getStatus() { return status; }
    public void setStatus(AttendanceStatus status) { this.status = status; }

    public LocalDateTime getMarkedAt() { return markedAt; }
    public void setMarkedAt(LocalDateTime markedAt) { this.markedAt = markedAt; }

    // Manual setter for nested 'student' object from frontend
    @JsonProperty("student")
    public void setStudent(Map<String, Object> student) {
        if (student != null) {
            if (student.get("id") != null) {
                this.studentId = Long.valueOf(student.get("id").toString());
            } else if (student.get("studentId") != null) {
                this.studentId = Long.valueOf(student.get("studentId").toString());
            }
            
            if (student.get("studentName") != null) {
                this.studentName = student.get("studentName").toString();
            } else if (student.get("name") != null) {
                this.studentName = student.get("name").toString();
            }
        }
    }
}