package com.lms.www.campus.student;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.lms.www.dto.StudentApiContract.*;
import com.lms.www.campus.Transport.*;
import com.lms.www.security.UserContext;
import com.lms.www.campus.service.student.impl.StudentTransportServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/student/transport")
public class StudentTransportController {

    private static final Logger log = LoggerFactory.getLogger(StudentTransportController.class);

    @Autowired
    private StudentTransportServiceImpl transportService;

    @Autowired
    private UserContext userContext;

    @GetMapping("/my-route")
    @PreAuthorize("hasAuthority('ROUTE_VIEW')")
    public ApiResponse<List<TransportAllocationDto>> getMyRoute() {
        Long studentId = userContext.getCurrentUserId();
        log.info("Fetching transport route for studentId: {}", studentId);

        List<StudentTransportAssignment> assignments = transportService.getMyTransportAllocation(studentId);

        List<TransportAllocationDto> dtos = assignments.stream().map(a -> TransportAllocationDto.builder()
                .routeName(a.getRoute() != null ? a.getRoute().getRouteName() : "N/A")
                .vehicleNumber(a.getVehicle() != null ? a.getVehicle().getVehicleNumber() : "N/A")
                .pickupPoint(a.getPickupPoint())
                .dropPoint(a.getDropPoint())
                .shift(a.getShift() != null ? a.getShift().name() : null)
                .build()).collect(Collectors.toList());

        return ApiResponse.success(dtos, "Transport routes fetched successfully");
    }

    @GetMapping("/my-attendance")
    @PreAuthorize("hasAuthority('TRANSPORT_ATTENDANCE_VIEW')")
    public ApiResponse<List<TransportAttendanceDto>> getMyAttendance() {
        Long studentId = userContext.getCurrentUserId();

        List<TransportAttendance> attendanceList = transportService.getMyAttendance(studentId);
        List<TransportAttendanceDto> dtos = attendanceList.stream().map(a -> TransportAttendanceDto.builder()
                .attendanceDate(a.getAttendanceDate())
                .pickupStatus(a.getPickupStatus() != null ? a.getPickupStatus().name() : "N/A")
                .dropStatus(a.getDropStatus() != null ? a.getDropStatus().name() : "N/A")
                .routeName(a.getRoute() != null ? a.getRoute().getRouteName() : "N/A")
                .build()).collect(Collectors.toList());

        return ApiResponse.success(dtos, "Attendance fetched successfully");
    }

    @GetMapping("/fees")
    @PreAuthorize("hasAuthority('FEE_VIEW')")
    public ApiResponse<List<TransportFeeDto>> getMyFees() {
        Long studentId = userContext.getCurrentUserId();

        List<TransportPayments> payments = transportService.getMyFees(studentId);
        List<TransportFeeDto> dtos = payments.stream().map(p -> TransportFeeDto.builder()
                .totalFee(p.getAmount() != null ? p.getAmount().doubleValue() : 0.0)
                .amountPaid(p.getAmount() != null ? p.getAmount().doubleValue() : 0.0)
                .dueAmount(0.0)
                .status(p.getPaymentMode() != null ? p.getPaymentMode().name() : "PAID")
                .build()).collect(Collectors.toList());

        return ApiResponse.success(dtos, "Transport fees fetched successfully");
    }
}
