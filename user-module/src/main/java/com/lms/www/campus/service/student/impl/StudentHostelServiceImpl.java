package com.lms.www.campus.service.student.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.lms.www.campus.Hostel.HostelAttendance;
import com.lms.www.campus.Hostel.HostelComplaint;
import com.lms.www.campus.Hostel.StudentHealthIncident;
import com.lms.www.campus.Hostel.StudentHostelAllocation;
import com.lms.www.campus.Hostel.StudentHostelFee;
import com.lms.www.campus.Hostel.StudentVisitEntry;
import com.lms.www.campus.repository.Hostel.HostelAttendanceRepository;
import com.lms.www.campus.repository.Hostel.HostelComplaintRepository;
import com.lms.www.campus.repository.Hostel.StudentFeeRepository;
import com.lms.www.campus.repository.Hostel.StudentHealthIncidentRepository;
import com.lms.www.campus.repository.Hostel.StudentHostelAllocationRepository;
import com.lms.www.campus.repository.Hostel.StudentVisitEntryRepository;

@Service
public class StudentHostelServiceImpl {

    @Autowired
    private StudentHostelAllocationRepository allocationRepository;

    @Autowired
    private HostelAttendanceRepository hostelAttedanceRepository;

    @Autowired
    private HostelComplaintRepository hostelComplaintRepository;

    @Autowired
    private StudentVisitEntryRepository visitRepository;

    @Autowired
    private StudentHealthIncidentRepository incidentRepository;

    @Autowired
    private StudentFeeRepository studentFeeRepository;

    public Optional<StudentHostelAllocation> getMyHostelAllocation(Long studentId) {
        return allocationRepository.findByStudentIdAndStatus(studentId,
                StudentHostelAllocation.AllocationStatus.ACTIVE);
    }

    public List<HostelAttendance> getMyAttendance(Long studentId) {
        return hostelAttedanceRepository.findByStudentId(studentId);
    }

    public HostelComplaint createMyComplaint(Long studentId, HostelComplaint request) {
        StudentHostelAllocation allocation = getMyHostelAllocation(studentId)
                .orElseThrow(() -> new RuntimeException("No active hostel assigned. Cannot raise complaint."));

        request.setStudentId(studentId);
        request.setStudentName(allocation.getStudentName());
        request.setHostel(allocation.getHostel());
        request.setHostelName(allocation.getHostel().getHostelName());
        request.setRoom(allocation.getRoom());
        request.setRoomNumber(allocation.getRoom().getRoomNumber());
        request.setReportedDate(LocalDate.now());
        request.setStatus(HostelComplaint.ComplaintStatus.OPEN);
        return hostelComplaintRepository.save(request);
    }

    public List<HostelComplaint> getMyComplaints(Long studentId) {
        return hostelComplaintRepository.findByStudentId(studentId);
    }

    public StudentVisitEntry createMyVisit(Long studentId, StudentVisitEntry request) {
        StudentHostelAllocation allocation = getMyHostelAllocation(studentId)
                .orElseThrow(() -> new RuntimeException("No active hostel assigned. Cannot book visit."));

        request.setStudentId(studentId);
        request.setStudentName(allocation.getStudentName());
        request.setCreatedAt(LocalDate.now());
        request.setStatus(StudentVisitEntry.VisitStatus.SCHEDULED);
        return visitRepository.save(request);
    }

    public List<StudentVisitEntry> getMyVisits(Long studentId) {
        return visitRepository.findByStudentId(studentId);
    }

    public void cancelMyVisit(Long studentId, Long visitId) {
        StudentVisitEntry visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new RuntimeException("Visit not found"));

        if (!visit.getStudentId().equals(studentId)) {
            throw new AccessDeniedException("Cannot cancel another student's visit");
        }

        if (visit.getStatus() == StudentVisitEntry.VisitStatus.CHECKED_OUT) {
            throw new RuntimeException("Cannot cancel a completed visit");
        }

        visit.setStatus(StudentVisitEntry.VisitStatus.CANCELLED);
        visitRepository.save(visit);
    }

    public List<StudentHealthIncident> getMyHealthIncidents(Long studentId) {
        return incidentRepository.findByStudentIdAndIsDeletedFalse(studentId);
    }

    public List<StudentHostelFee> getMyFees(Long studentId) {
        return studentFeeRepository.findByStudentId(studentId);
    }
}
