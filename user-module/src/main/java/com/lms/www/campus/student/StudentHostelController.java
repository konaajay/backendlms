package com.lms.www.campus.student;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.www.dto.StudentApiContract.ApiResponse;
import com.lms.www.dto.StudentApiContract.HostelAllocationDto;
import com.lms.www.dto.StudentApiContract.HostelAttendanceDto;
import com.lms.www.dto.StudentApiContract.HostelComplaintDto;
import com.lms.www.dto.StudentApiContract.HostelFeeDto;
import com.lms.www.dto.StudentApiContract.HostelVisitDto;
import com.lms.www.security.UserContext;
import com.lms.www.campus.Hostel.HostelAttendance;
import com.lms.www.campus.Hostel.HostelComplaint;
import com.lms.www.campus.Hostel.StudentHostelAllocation;
import com.lms.www.campus.Hostel.StudentHostelFee;
import com.lms.www.campus.Hostel.StudentVisitEntry;
import com.lms.www.campus.service.student.impl.StudentHostelServiceImpl;

@RestController
@RequestMapping("/api/student/hostel")
public class StudentHostelController {

    private static final Logger log = LoggerFactory.getLogger(StudentHostelController.class);

    @Autowired
    private StudentHostelServiceImpl hostelService;

    @Autowired
    private UserContext userContext;

    @GetMapping("/my-hostel")
    @PreAuthorize("hasAuthority('HOSTEL_VIEW')")
    public ApiResponse<HostelAllocationDto> getMyHostel() {
        Long studentId = userContext.getCurrentUserId();
        log.info("Fetching hostel allocation for studentId: {}", studentId);

        StudentHostelAllocation allocation = hostelService.getMyHostelAllocation(studentId)
                .orElseThrow(() -> new RuntimeException("No active hostel assigned"));

        HostelAllocationDto dto = HostelAllocationDto.builder()
                .hostelName(allocation.getHostelName())
                .roomNumber(allocation.getRoomNumber())
                .joinDate(allocation.getJoinDate())
                .status(allocation.getStatus().name())
                .build();

        return ApiResponse.success(dto, "Hostel details fetched successfully");
    }

    @GetMapping("/my-attendance")
    @PreAuthorize("hasAuthority('HOSTEL_ATTENDANCE_VIEW')")
    public ApiResponse<List<HostelAttendanceDto>> getMyAttendance() {
        Long studentId = userContext.getCurrentUserId();

        List<HostelAttendance> attendanceList = hostelService.getMyAttendance(studentId);
        List<HostelAttendanceDto> dtos = attendanceList.stream().map(a -> HostelAttendanceDto.builder()
                .attendanceDate(a.getAttendanceDate())
                .status(a.getStatus().name())
                .build()).collect(Collectors.toList());

        return ApiResponse.success(dtos, "Attendance fetched successfully");
    }

    @PostMapping("/complaints")
    @PreAuthorize("hasAuthority('HOSTEL_COMPLAINT_CREATE')")
    public ApiResponse<HostelComplaintDto> raiseComplaint(@RequestBody HostelComplaint request) {
        Long studentId = userContext.getCurrentUserId();
        log.info("Raising complaint for studentId: {}", studentId);

        HostelComplaint complaint = hostelService.createMyComplaint(studentId, request);

        HostelComplaintDto dto = HostelComplaintDto.builder()
                .complaintId(complaint.getComplaintId())
                .issueCategory(complaint.getIssueCategory().name())
                .description(complaint.getDescription())
                .reportedDate(complaint.getReportedDate())
                .status(complaint.getStatus().name())
                .adminRemarks(complaint.getAdminRemarks())
                .build();

        return ApiResponse.success(dto, "Complaint raised successfully");
    }

    @GetMapping("/complaints")
    @PreAuthorize("hasAuthority('HOSTEL_COMPLAINT_VIEW')")
    public ApiResponse<List<HostelComplaintDto>> getMyComplaints() {
        Long studentId = userContext.getCurrentUserId();

        List<HostelComplaint> complaints = hostelService.getMyComplaints(studentId);
        List<HostelComplaintDto> dtos = complaints.stream().map(c -> HostelComplaintDto.builder()
                .complaintId(c.getComplaintId())
                .issueCategory(c.getIssueCategory().name())
                .description(c.getDescription())
                .reportedDate(c.getReportedDate())
                .status(c.getStatus().name())
                .adminRemarks(c.getAdminRemarks())
                .build()).collect(Collectors.toList());

        return ApiResponse.success(dtos, "Complaints fetched successfully");
    }

    @PostMapping("/visit")
    public ApiResponse<HostelVisitDto> bookVisit(@RequestBody StudentVisitEntry request) {
        Long studentId = userContext.getCurrentUserId();
        log.info("Booking visit for studentId: {}", studentId);

        StudentVisitEntry visit = hostelService.createMyVisit(studentId, request);

        HostelVisitDto dto = HostelVisitDto.builder()
                .visitId(visit.getVisitId())
                .visitorName(visit.getVisitorName())
                .visitDate(visit.getVisitDate())
                .status(visit.getStatus().name())
                .build();

        return ApiResponse.success(dto, "Visit booked successfully");
    }

    @GetMapping("/visit")
    public ApiResponse<List<HostelVisitDto>> getMyVisits() {
        Long studentId = userContext.getCurrentUserId();

        List<StudentVisitEntry> visits = hostelService.getMyVisits(studentId);
        List<HostelVisitDto> dtos = visits.stream().map(v -> HostelVisitDto.builder()
                .visitId(v.getVisitId())
                .visitorName(v.getVisitorName())
                .visitDate(v.getVisitDate())
                .status(v.getStatus().name())
                .build()).collect(Collectors.toList());

        return ApiResponse.success(dtos, "Visits fetched successfully");
    }

    @DeleteMapping("/visit/{id}")
    public ApiResponse<String> cancelVisit(@PathVariable Long id) {
        Long studentId = userContext.getCurrentUserId();
        log.info("Canceling visit {} for studentId: {}", id, studentId);

        hostelService.cancelMyVisit(studentId, id);
        return ApiResponse.success("Cancelled", "Visit cancelled successfully");
    }

    @GetMapping("/fees")
    @PreAuthorize("hasAuthority('HOSTEL_FEE_VIEW')")
    public ApiResponse<List<HostelFeeDto>> getMyFees() {
        Long studentId = userContext.getCurrentUserId();

        List<StudentHostelFee> fees = hostelService.getMyFees(studentId);
        List<HostelFeeDto> dtos = fees.stream().map(f -> HostelFeeDto.builder()
                .totalFee(f.getTotalFee())
                .amountPaid(f.getAmountPaid())
                .dueAmount(f.getDueAmount())
                .status(f.getStatus().name())
                .build()).collect(Collectors.toList());

        return ApiResponse.success(dtos, "Fees fetched successfully");
    }
}
