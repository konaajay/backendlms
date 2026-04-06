package com.lms.www.campus.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.lms.www.campus.Hostel.Hostel;
import com.lms.www.campus.Hostel.HostelAttendance;
import com.lms.www.campus.Hostel.HostelComplaint;
import com.lms.www.campus.Hostel.HostelRoom;
import com.lms.www.campus.Hostel.MessDayMenu;
import com.lms.www.campus.Hostel.StudentHealthIncident;
import com.lms.www.campus.Hostel.StudentHostelAllocation;
import com.lms.www.campus.Hostel.StudentHostelFee;
import com.lms.www.campus.Hostel.StudentVisitEntry;
import com.lms.www.campus.Hostel.StudentHostelAllocation.AllocationStatus;
import com.lms.www.campus.repository.Hostel.HostelAttendanceRepository;
import com.lms.www.campus.repository.Hostel.HostelComplaintRepository;
import com.lms.www.campus.repository.Hostel.HostelRepository;
import com.lms.www.campus.repository.Hostel.HostelRoomRepository;
import com.lms.www.campus.repository.Hostel.MessDayMenuRepository;
import com.lms.www.campus.repository.Hostel.StudentFeeRepository;
import com.lms.www.campus.repository.Hostel.StudentHealthIncidentRepository;
import com.lms.www.campus.repository.Hostel.StudentHostelAllocationRepository;
import com.lms.www.campus.repository.Hostel.StudentVisitEntryRepository;

import jakarta.transaction.Transactional;

@Service
public class HostelServiceImpl {

    @Autowired
    private HostelRepository hostelRepository;

    @Autowired
    private HostelAttendanceRepository hostelAttedanceRepository;

    @Autowired
    private HostelRoomRepository hostelRoomRepository;

    @Autowired
    private StudentHealthIncidentRepository incidentRepository;

    @Autowired
    private MessDayMenuRepository messDayMenuRepository;

    @Autowired
    private StudentHostelAllocationRepository allocationRepository;

    @Autowired
    private HostelComplaintRepository complaintRepository;

    @Autowired
    private StudentVisitEntryRepository visitRepository;

    @Autowired
    private StudentFeeRepository studentFeeRepository;

    // ================= HOSTEL =================

    public Hostel createHostel(Hostel hostel) {
        if (hostelRepository.existsByHostelNameAndIsDeletedFalse(hostel.getHostelName())) {
            throw new RuntimeException("A hostel with the name '" + hostel.getHostelName() + "' already exists.");
        }
        return hostelRepository.save(hostel);
    }

    // GET ALL
    public List<Hostel> getAllHostels() {
        return hostelRepository.findByIsDeletedFalse();
    }

    // GET BY ID
    public Hostel getHostelById(Long hostelId) {
        return hostelRepository.findById(hostelId)
                .filter(h -> !Boolean.TRUE.equals(h.getIsDeleted()))
                .orElseThrow(() -> new RuntimeException("Hostel not found"));
    }

    // PUT Method
    public Hostel updateHostel(Long id, Hostel hostel) {

        Hostel existingHostel = hostelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hostel not found"));

        existingHostel.setHostelName(hostel.getHostelName());
        existingHostel.setWardenName(hostel.getWardenName());
        existingHostel.setContactNumber(hostel.getContactNumber());
        existingHostel.setHostelType(hostel.getHostelType());
        existingHostel.setTotalBlocks(hostel.getTotalBlocks());
        existingHostel.setTotalRooms(hostel.getTotalRooms());
        existingHostel.setStatus(hostel.getStatus());

        return hostelRepository.save(existingHostel);
    }

    // PATCH
    public Hostel updateHostelPartial(Long hostelId, Hostel request) {
        Hostel hostel = getHostelById(hostelId);

        if (request.getHostelName() != null)
            hostel.setHostelName(request.getHostelName());

        if (request.getHostelType() != null)
            hostel.setHostelType(request.getHostelType());

        if (request.getWardenName() != null)
            hostel.setWardenName(request.getWardenName());

        if (request.getContactNumber() != null)
            hostel.setContactNumber(request.getContactNumber());

        return hostelRepository.save(hostel);
    }

    // PATCH STATUS
    public Hostel updateStatus(Long hostelId, Hostel.Status status) {
        Hostel hostel = getHostelById(hostelId);
        hostel.setStatus(status);
        return hostelRepository.save(hostel);
    }

    // SOFT DELETE
    public void deleteHostel(Long hostelId) {
        Hostel hostel = getHostelById(hostelId);
        hostel.setIsDeleted(true);
        hostelRepository.save(hostel);
    }

    // =================HostelAttendance===========================================//

    @Transactional
    public HostelAttendance markAttendance(HostelAttendance request) {

        if (request.getStudentId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "STUDENT_ID_REQUIRED");
        }

        Long studentId = request.getStudentId();
        StudentHostelAllocation allocation = allocationRepository
                .findByStudentIdAndStatus(
                        studentId,
                        AllocationStatus.ACTIVE)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "ROOM_NOT_ASSIGNED"));

        HostelRoom room = allocation.getRoom();
        if (room == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "ROOM_NOT_ASSIGNED");
        }

        boolean alreadyMarked = hostelAttedanceRepository
                .existsByStudentIdAndAttendanceDate(
                        studentId,
                        LocalDate.now());

        if (alreadyMarked) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "ATTENDANCE_ALREADY_MARKED");
        }

        HostelAttendance attendance = new HostelAttendance();
        attendance.setStudentId(allocation.getStudentId());
        attendance.setStudentName(allocation.getStudentName());
        attendance.setRoom(room);
        attendance.setRoomNumber(room.getRoomNumber());
        attendance.setStatus(request.getStatus()); // PRESENT / ABSENT
        attendance.setAttendanceDate(LocalDate.now());
        attendance.setMarkedAt(LocalDateTime.now());

        return hostelAttedanceRepository.save(attendance);
    }

    // GET ALL ATTENDANCE
    public List<HostelAttendance> getAllAttendance(LocalDate date) {
        if (date != null) {
            return hostelAttedanceRepository.findByAttendanceDate(date);
        }
        return hostelAttedanceRepository.findAll();
    }

    // GET BY ID
    public HostelAttendance getAttendanceById(Long attendanceId) {
        return hostelAttedanceRepository.findById(attendanceId)
                .orElseThrow(() -> new RuntimeException("Attendance not found"));
    }

    // PATCH (PARTIAL UPDATE)
    public HostelAttendance updateAttendancePartial(Long attendanceId, HostelAttendance request) {
        HostelAttendance attendance = getAttendanceById(attendanceId);

        if (request.getStatus() != null)
            attendance.setStatus(request.getStatus());

        return hostelAttedanceRepository.save(attendance);
    }

    // DELETE
    public void deleteAttendance(Long attendanceId) {
        hostelAttedanceRepository.deleteById(attendanceId);
    }

    // ====================HostelComplaints===============================//

    // GET ALL COMPLAINTS (OPTIONAL FILTER BY STATUS)
    public List<HostelComplaint> getAllComplaints(
            HostelComplaint.ComplaintStatus status) {

        if (status != null) {
            return complaintRepository.findByStatus(status);
        }
        return complaintRepository.findAll();
    }

    // GET COMPLAINT BY ID
    public HostelComplaint getComplaintById(Long complaintId) {
        return complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
    }

    public HostelComplaint updateComplaintFull(Long id, HostelComplaint updated) {

        HostelComplaint existing = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found with id: " + id));

        // -------- STUDENT INFO --------
        existing.setStudentId(updated.getStudentId());
        existing.setStudentName(updated.getStudentName());

        // -------- COMPLAINT DETAILS --------
        existing.setIssueCategory(updated.getIssueCategory());
        existing.setPriority(updated.getPriority());
        existing.setDescription(updated.getDescription());
        existing.setReportedDate(updated.getReportedDate());
        existing.setStatus(updated.getStatus());
        existing.setAdminRemarks(updated.getAdminRemarks());

        return complaintRepository.save(existing);
    }

    // PATCH
    public HostelComplaint updateComplaint(
            Long complaintId,
            HostelComplaint.ComplaintStatus status,
            String adminRemarks) {

        HostelComplaint complaint = getComplaintById(complaintId);

        if (status != null)
            complaint.setStatus(status);

        if (adminRemarks != null)
            complaint.setAdminRemarks(adminRemarks);

        return complaintRepository.save(complaint);
    }

    // DELETE
    public void deleteComplaint(Long complaintId) {
        complaintRepository.deleteById(complaintId);
    }
    // ================================ HostelRooms ==============================//

    // CREATE ROOM
    public HostelRoom createRoom(HostelRoom room) {
        if (room.getCurrentlyOccupied() == null) {
            room.setCurrentlyOccupied(0);
        }
        return hostelRoomRepository.save(room);
    }

    // GET ALL ROOMS
    public List<HostelRoom> getAllRooms() {
        return hostelRoomRepository.findByIsDeletedFalse();
    }

    // GET ROOM BY ID
    public HostelRoom getRoomById(Long roomId) {
        return hostelRoomRepository.findById(roomId)
                .filter(r -> !Boolean.TRUE.equals(r.getIsDeleted()))
                .orElseThrow(() -> new RuntimeException("Room not found"));
    }

    // PATCH (PARTIAL UPDATE)
    public HostelRoom updateRoomPartial(Long roomId, HostelRoom request) {
        HostelRoom room = getRoomById(roomId);

        if (request.getRoomNumber() != null)
            room.setRoomNumber(request.getRoomNumber());

        if (request.getSharingType() != null)
            room.setSharingType(request.getSharingType());

        return hostelRoomRepository.save(room);
    }

    // PATCH STATUS
    public HostelRoom updateRoomStatus(Long roomId, HostelRoom.RoomStatus status) {
        HostelRoom room = getRoomById(roomId);
        room.setStatus(status);
        return hostelRoomRepository.save(room);
    }

    // Put
    public HostelRoom updateRoom(Long id, HostelRoom room) {

        HostelRoom existingRoom = hostelRoomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        if (room.getRoomNumber() != null)
            existingRoom.setRoomNumber(room.getRoomNumber());

        if (room.getSharingType() != null)
            existingRoom.setSharingType(room.getSharingType());

        if (room.getStatus() != null)
            existingRoom.setStatus(room.getStatus());

        if (room.getCurrentlyOccupied() != null) {
            existingRoom.setCurrentlyOccupied(room.getCurrentlyOccupied());
        }

        if (room.getIsDeleted() != null) {
            existingRoom.setIsDeleted(room.getIsDeleted());
        }

        return hostelRoomRepository.save(existingRoom);
    }

    // SOFT DELETE
    public void deleteRoom(Long roomId) {
        HostelRoom room = getRoomById(roomId);
        room.setIsDeleted(true);
        hostelRoomRepository.save(room);
    }
    // ==================MessMenu===========================//

    public MessDayMenu createMenu(MessDayMenu menu) {
        messDayMenuRepository.findByDay(menu.getDay())
                .ifPresent(existing -> {
                    throw new RuntimeException("Menu already exists for " + menu.getDay());
                });
        return messDayMenuRepository.save(menu);
    }

    // GET ALL MENUS
    public List<MessDayMenu> getAllMenus() {
        return messDayMenuRepository.findAll();
    }

    // GET MENU BY ID
    public MessDayMenu getMenuById(Long menuId) {
        return messDayMenuRepository.findById(menuId)
                .orElseThrow(() -> new RuntimeException("Menu not found"));
    }

    // PUT
    public MessDayMenu updateMenu(Long id, MessDayMenu menu) {
        MessDayMenu existingMenu = messDayMenuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mess menu not found"));
        existingMenu.setDay(menu.getDay());
        existingMenu.setBreakfast(menu.getBreakfast());
        existingMenu.setLunch(menu.getLunch());
        existingMenu.setDinner(menu.getDinner());
        return messDayMenuRepository.save(existingMenu);
    }

    // PATCH
    public MessDayMenu updateMenuPartial(Long menuId, MessDayMenu request) {
        MessDayMenu menu = getMenuById(menuId);

        if (request.getBreakfast() != null)
            menu.setBreakfast(request.getBreakfast());

        if (request.getLunch() != null)
            menu.setLunch(request.getLunch());

        if (request.getDinner() != null)
            menu.setDinner(request.getDinner());

        return messDayMenuRepository.save(menu);
    }

    // DELETE
    public void deleteMenu(Long menuId) {
        messDayMenuRepository.deleteById(menuId);
    }

    // ================StudentHealth==================================================//

    // CREATE INCIDENT
    @Transactional
    public StudentHealthIncident createIncident(StudentHealthIncident incident) {
        if (incident.getReportedDate() == null) {
            incident.setReportedDate(LocalDate.now());
        }

        // 1. Resolve via Active Allocation (Most Robust)
        if (incident.getStudentId() != null) {
            allocationRepository.findByStudentIdAndStatus(incident.getStudentId(), AllocationStatus.ACTIVE)
                    .ifPresent(alloc -> {
                        if (incident.getHostel() == null) incident.setHostel(alloc.getHostel());
                        if (incident.getRoom() == null) incident.setRoom(alloc.getRoom());
                        if (incident.getHostelName() == null) incident.setHostelName(alloc.getHostelName());
                        if (incident.getRoomNumber() == null) incident.setRoomNumber(alloc.getRoomNumber());
                    });
        }

        // 2. Fallback to Name-based resolution for Hostel
        if (incident.getHostel() == null && incident.getHostelName() != null) {
            hostelRepository.findByHostelName(incident.getHostelName())
                    .ifPresent(incident::setHostel);
        }

        // 3. Fallback to RoomNumber resolution (Safe if unique globally)
        if (incident.getRoom() == null && incident.getRoomNumber() != null) {
            hostelRoomRepository.findByRoomNumber(incident.getRoomNumber())
                    .ifPresent(incident::setRoom);
        }

        return incidentRepository.save(incident);
    }

    // GET ALL INCIDENTS
    public List<StudentHealthIncident> getAllIncidents() {
        return incidentRepository.findByIsDeletedFalse();
    }

    // GET INCIDENT BY ID
    public StudentHealthIncident getIncidentById(Long incidentId) {
        return incidentRepository.findById(incidentId)
                .filter(i -> !Boolean.TRUE.equals(i.getIsDeleted()))
                .orElseThrow(() -> new RuntimeException("Health incident not found"));
    }

    // PATCH – UPDATE STATUS / NOTES ONLY
    @Transactional
    public StudentHealthIncident updateIncident(
            Long incidentId,
            StudentHealthIncident.IncidentStatus status,
            String clinicalNotes) {

        StudentHealthIncident incident = getIncidentById(incidentId);

        if (status != null)
            incident.setCurrentStatus(status);

        if (clinicalNotes != null)
            incident.setClinicalNotes(clinicalNotes);

        return incidentRepository.save(incident);
    }

    // Put method
    @Transactional
    public StudentHealthIncident updateIncidentFull(
            Long id,
            StudentHealthIncident updated) {

        StudentHealthIncident existing = getIncidentById(id);

        existing.setStudentId(updated.getStudentId());
        existing.setStudentName(updated.getStudentName());
        existing.setStudentPhone(updated.getStudentPhone());
        existing.setParentPhone(updated.getParentPhone());
        existing.setComplaintNature(updated.getComplaintNature());
        existing.setSeverity(updated.getSeverity());
        existing.setCurrentStatus(updated.getCurrentStatus());
        existing.setReportedDate(updated.getReportedDate());
        existing.setClinicalNotes(updated.getClinicalNotes());
        
        // Robust resolution via Allocation if studentId provided
        if (updated.getStudentId() != null) {
            allocationRepository.findByStudentIdAndStatus(updated.getStudentId(), AllocationStatus.ACTIVE)
                .ifPresent(alloc -> {
                    existing.setHostel(alloc.getHostel());
                    existing.setRoom(alloc.getRoom());
                    existing.setHostelName(alloc.getHostelName());
                    existing.setRoomNumber(alloc.getRoomNumber());
                });
        }
        
        // Fallback name-to-entity resolution
        if (existing.getHostel() == null && updated.getHostelName() != null) {
            existing.setHostelName(updated.getHostelName());
            hostelRepository.findByHostelName(updated.getHostelName())
                    .ifPresent(existing::setHostel);
        }
        
        if (existing.getRoom() == null && updated.getRoomNumber() != null) {
            existing.setRoomNumber(updated.getRoomNumber());
            hostelRoomRepository.findByRoomNumber(updated.getRoomNumber())
                    .ifPresent(existing::setRoom);
        }

        return incidentRepository.save(existing);
    }

    // SOFT DELETE
    public void deleteIncident(Long incidentId) {
        StudentHealthIncident incident = getIncidentById(incidentId);
        incident.setIsDeleted(true);
        incidentRepository.save(incident);
    }

    // ========================= Students Hostel
    // Fees==============================//
    // post
    @Transactional
    public StudentHostelFee createFee(StudentHostelFee request) {
        StudentHostelFee fee = new StudentHostelFee();
        fee.setStudentId(request.getStudentId());
        fee.setStudentName(request.getStudentName());
        fee.setMonthlyFee(request.getMonthlyFee());
        fee.setTotalFee(request.getTotalFee());
        fee.setAmountPaid(0.0);
        fee.setDueAmount(request.getTotalFee());
        fee.setStatus(StudentHostelFee.FeeStatus.DUE);
        fee.setLastPaymentDate(null);
        return studentFeeRepository.save(fee);
    }

    // GET BY ID
    public StudentHostelFee getFeeById(Long id) {
        return studentFeeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Fee not found with id: " + id));
    }

    // Get
    public List<StudentHostelFee> getAllFees(StudentHostelFee.FeeStatus status) {

        if (status != null) {
            return studentFeeRepository.findByStatus(status);
        }
        return studentFeeRepository.findAll();
    }

    // Patch
    @Transactional
    public StudentHostelFee updatePayment(Long feeId, Double amountPaid) {
        StudentHostelFee fee = getFeeById(feeId);
        double newPaid = fee.getAmountPaid() + amountPaid;
        double due = fee.getTotalFee() - newPaid;
        fee.setAmountPaid(newPaid);
        fee.setDueAmount(Math.max(due, 0.0));
        fee.setLastPaymentDate(LocalDate.now());
        if (due <= 0) {
            fee.setStatus(StudentHostelFee.FeeStatus.PAID);
        } else {
            fee.setStatus(StudentHostelFee.FeeStatus.PARTIALLY_PAID);
        }
        return studentFeeRepository.save(fee);
    }

    // put
    @Transactional
    public StudentHostelFee updateFee(Long id, StudentHostelFee request) {
        StudentHostelFee existing = getFeeById(id);
        existing.setStudentName(request.getStudentName());
        existing.setMonthlyFee(request.getMonthlyFee());
        existing.setTotalFee(request.getTotalFee());
        double due = existing.getTotalFee() - existing.getAmountPaid();
        existing.setDueAmount(Math.max(due, 0.0));
        if (due <= 0) {
            existing.setStatus(StudentHostelFee.FeeStatus.PAID);
        } else if (existing.getAmountPaid() > 0) {
            existing.setStatus(StudentHostelFee.FeeStatus.PARTIALLY_PAID);
        } else {
            existing.setStatus(StudentHostelFee.FeeStatus.DUE);
        }
        return studentFeeRepository.save(existing);
    }

    // Delete
    @Transactional
    public void cancelFee(Long id) {

        StudentHostelFee fee = studentFeeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Fee not found"));

        fee.setStatus(StudentHostelFee.FeeStatus.DUE);
        fee.setDueAmount(fee.getTotalFee());
        fee.setAmountPaid(0.0);

        studentFeeRepository.save(fee);
    }

    // ================= StudentHostelAllocationService=================

    // CREATE ALLOCATION
    @Transactional
    public StudentHostelAllocation createAllocation(StudentHostelAllocation request) {
        // Robust ID extraction
        Long hostelId = (request.getHostel() != null) ? request.getHostel().getHostelId() : null;
        Long roomId = (request.getRoom() != null) ? request.getRoom().getRoomId() : null;

        // Validation with clear error messages
        if (hostelId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "HOSTEL_NOT_SPECIFIED");
        }
        if (roomId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ROOM_NOT_SPECIFIED");
        }

        // Fetch managed entities
        Hostel hostel = hostelRepository.findById(hostelId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "HOSTEL_NOT_FOUND"));

        HostelRoom room = hostelRoomRepository.findById(roomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ROOM_NOT_FOUND"));

        StudentHostelAllocation allocation = new StudentHostelAllocation();
        allocation.setStudentId(request.getStudentId());
        allocation.setStudentName(request.getStudentName());
        allocation.setStudentEmail(request.getStudentEmail());
        allocation.setParentName(request.getParentName());
        allocation.setParentPhone(request.getParentPhone());
        allocation.setJoinDate(request.getJoinDate());
        allocation.setLeaveDate(request.getLeaveDate());
        allocation.setStatus(request.getStatus() != null ? request.getStatus() : StudentHostelAllocation.AllocationStatus.ACTIVE);
        
        allocation.setHostel(hostel);
        allocation.setHostelName(hostel.getHostelName());
        allocation.setRoom(room);
        allocation.setRoomNumber(room.getRoomNumber());

        return allocationRepository.save(allocation);
    }

    // GET ALL ALLOCATIONS
    public List<StudentHostelAllocation> getAllAllocations(
            StudentHostelAllocation.AllocationStatus status) {
        if (status != null) {
            return allocationRepository.findByStatus(status);
        }
        return allocationRepository.findAll();
    }

    // GET ALLOCATION BY ID
    public StudentHostelAllocation getAllocationById(Long allocationId) {
        return allocationRepository.findById(allocationId)
                .orElseThrow(() -> new RuntimeException("Allocation not found"));
    }

    // PATCH
    public StudentHostelAllocation updateAllocationStatus(
            Long allocationId,
            StudentHostelAllocation.AllocationStatus status,
            LocalDate leaveDate) {

        StudentHostelAllocation allocation = getAllocationById(allocationId);

        if (status != null) {
            allocation.setStatus(status);
        }

        if (leaveDate != null) {
            allocation.setLeaveDate(leaveDate);
        }

        return allocationRepository.save(allocation);
    }

    // Put Method
    @Transactional
    public StudentHostelAllocation updateAllocation(Long id, StudentHostelAllocation allocation) {
        StudentHostelAllocation existing = allocationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Allocation not found with id: " + id));

        existing.setStudentName(allocation.getStudentName());
        existing.setStudentEmail(allocation.getStudentEmail());
        existing.setParentName(allocation.getParentName());
        existing.setParentPhone(allocation.getParentPhone());
        existing.setJoinDate(allocation.getJoinDate());
        existing.setLeaveDate(allocation.getLeaveDate());
        existing.setStatus(allocation.getStatus());

        // Update Hostel and Room if provided
        Long hostelId = (allocation.getHostel() != null) ? allocation.getHostel().getHostelId() : null;
        Long roomId = (allocation.getRoom() != null) ? allocation.getRoom().getRoomId() : null;

        if (hostelId != null) {
            Hostel hostel = hostelRepository.findById(hostelId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "HOSTEL_NOT_FOUND"));
            existing.setHostel(hostel);
            existing.setHostelName(hostel.getHostelName());
        }

        if (roomId != null) {
            HostelRoom room = hostelRoomRepository.findById(roomId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ROOM_NOT_FOUND"));
            existing.setRoom(room);
            existing.setRoomNumber(room.getRoomNumber());
        }

        return allocationRepository.save(existing);
    }

    public void deleteAllocation(Long id) {
        StudentHostelAllocation allocation = allocationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Allocation not found"));
        allocation.setStatus(StudentHostelAllocation.AllocationStatus.CANCELLED);
        allocation.setLeaveDate(LocalDate.now());
        allocationRepository.save(allocation);
    }

    public void hardDeleteAllocation(Long id) {
        allocationRepository.deleteById(id);
    }

    // ================= Hostel complaints =================

    @Transactional
    public HostelComplaint createComplaint(HostelComplaint request) {
        String hostelName = request.getHostelName();
        String roomNumber = request.getRoomNumber();

        if (hostelName == null || hostelName.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "HOSTEL_NAME_REQUIRED");
        }
        if (roomNumber == null || roomNumber.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ROOM_NUMBER_REQUIRED");
        }

        Hostel hostel = hostelRepository.findByHostelName(hostelName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "HOSTEL_NOT_FOUND"));
        HostelRoom room = hostelRoomRepository.findByRoomNumber(roomNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ROOM_NOT_FOUND"));

        HostelComplaint complaint = new HostelComplaint();
        complaint.setStudentId(request.getStudentId());
        complaint.setStudentName(request.getStudentName());
        complaint.setHostel(hostel);
        complaint.setRoom(room);
        complaint.setHostelName(hostel.getHostelName());
        complaint.setRoomNumber(room.getRoomNumber());
        complaint.setIssueCategory(request.getIssueCategory());
        complaint.setPriority(request.getPriority());
        complaint.setDescription(request.getDescription());
        complaint.setReportedDate(request.getReportedDate());
        complaint.setStatus(request.getStatus() != null ? request.getStatus() : HostelComplaint.ComplaintStatus.OPEN);
        complaint.setAdminRemarks(request.getAdminRemarks());

        return complaintRepository.save(complaint);
    }

    // READ
    public List<HostelComplaint> getAllComplaints() {
        return complaintRepository.findAll();
    }

    public HostelComplaint getById(Long id) {
        return complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
    }

    // PATCH
    public HostelComplaint patchComplaint(Long id, HostelComplaint req) {
        HostelComplaint existing = getById(id);

        if (req.getIssueCategory() != null)
            existing.setIssueCategory(req.getIssueCategory());

        if (req.getPriority() != null)
            existing.setPriority(req.getPriority());

        if (req.getDescription() != null)
            existing.setDescription(req.getDescription());

        if (req.getStatus() != null)
            existing.setStatus(req.getStatus());

        if (req.getAdminRemarks() != null)
            existing.setAdminRemarks(req.getAdminRemarks());

        return complaintRepository.save(existing);
    }

    // DELETE
    public void closeComplaint(Long id) {
        HostelComplaint complaint = getById(id);
        complaint.setStatus(HostelComplaint.ComplaintStatus.CLOSED);
        complaintRepository.save(complaint);
    }

    // ====================StudentVisitEntryService====================//

    // CREATE
    public StudentVisitEntry createVisit(StudentVisitEntry visit) {
        visit.setCreatedAt(LocalDate.now());

        return visitRepository.save(visit);
    }

    // GET
    public List<StudentVisitEntry> getAllVisits(LocalDate date) {
        if (date != null) {
            return visitRepository.findByVisitDate(date);
        }
        return visitRepository.findAll();
    }

    // GET
    public StudentVisitEntry getVisitById(Long visitId) {
        return visitRepository.findById(visitId)
                .orElseThrow(() -> new RuntimeException("Visit entry not found"));
    }

    // PATCH
    public StudentVisitEntry updateVisitStatus(
            Long visitId,
            StudentVisitEntry.VisitStatus status) {

        StudentVisitEntry visit = getVisitById(visitId);
        visit.setStatus(status);
        return visitRepository.save(visit);
    }

    public StudentVisitEntry updateVisit(Long id, StudentVisitEntry visit) {
        StudentVisitEntry existing = visitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Visit entry not found with id: " + id));
        existing.setStudentId(visit.getStudentId());
        existing.setStudentName(visit.getStudentName());
        existing.setVisitorName(visit.getVisitorName());
        existing.setVisitorContact(visit.getVisitorContact());
        existing.setPurposeOfVisit(visit.getPurposeOfVisit());
        existing.setVisitDate(visit.getVisitDate());
        existing.setVisitTime(visit.getVisitTime());
        existing.setStatus(visit.getStatus());
        return visitRepository.save(existing);
    }

    // DELETE
    public void deleteVisit(Long visitId) {
        visitRepository.deleteById(visitId);
    }

}
