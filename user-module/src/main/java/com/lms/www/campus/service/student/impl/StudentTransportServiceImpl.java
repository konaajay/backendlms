package com.lms.www.campus.service.student.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lms.www.campus.Transport.StudentTransportAssignment;
import com.lms.www.campus.Transport.TransportAttendance;
import com.lms.www.campus.Transport.TransportPayments;
import com.lms.www.campus.repository.Transport.StudentTransportAssignmentRepository;
import com.lms.www.campus.repository.Transport.TransportAttendanceRepository;
import com.lms.www.campus.repository.Transport.TransportPaymentRepository;

@Service
public class StudentTransportServiceImpl {

    @Autowired
    private StudentTransportAssignmentRepository studentTransportAssignRepo;

    @Autowired
    private TransportAttendanceRepository attendanceRepository;

    @Autowired
    private TransportPaymentRepository paymentRepository;

    public List<StudentTransportAssignment> getMyTransportAllocation(Long studentId) {
        List<StudentTransportAssignment> assignments = studentTransportAssignRepo.findByStudentId(studentId);
        if (assignments.isEmpty()) {
            throw new RuntimeException(
                    "No active transport route assigned for this student (Day Scholar or not assigned).");
        }
        return assignments;
    }

    public List<TransportAttendance> getMyAttendance(Long studentId) {
        return attendanceRepository.findByStudentId(studentId);
    }

    public List<TransportPayments> getMyFees(Long studentId) {
        return paymentRepository.findByStudentId(studentId);
    }
}
