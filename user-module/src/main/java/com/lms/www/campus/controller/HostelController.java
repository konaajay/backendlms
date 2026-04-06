package com.lms.www.campus.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.lms.www.campus.Hostel.*;
import com.lms.www.campus.service.impl.HostelServiceImpl;

@RestController
@RequestMapping("/campus")
public class HostelController {

    @Autowired
    private HostelServiceImpl hostelService;

    // ================= HOSTEL =================

    @PostMapping("/hostel")
    public ResponseEntity<Hostel> createHostel(@RequestBody Hostel hostel) {
        return ResponseEntity.ok(hostelService.createHostel(hostel));
    }

    @GetMapping("/hostels")
    public ResponseEntity<List<Hostel>> getAllHostels() {
        return ResponseEntity.ok(hostelService.getAllHostels());
    }

    @GetMapping("/hostels/{id}")
    public ResponseEntity<Hostel> getHostelById(@PathVariable Long id) {
        return ResponseEntity.ok(hostelService.getHostelById(id));
    }

    @PutMapping("/hostel/{id}")
    public ResponseEntity<Hostel> updateHostel(@PathVariable Long id, @RequestBody Hostel hostel) {
        return ResponseEntity.ok(hostelService.updateHostel(id, hostel));
    }

    @PatchMapping("/hostel/{id}")
    public ResponseEntity<Hostel> updateHostelPartial(@PathVariable Long id, @RequestBody Hostel hostel) {
        return ResponseEntity.ok(hostelService.updateHostelPartial(id, hostel));
    }

    @DeleteMapping("/hostel/{id}")
    public ResponseEntity<Void> deleteHostel(@PathVariable Long id) {
        hostelService.deleteHostel(id);
        return ResponseEntity.noContent().build();
    }

    // ================= HOSTEL ROOM =================

    @PostMapping("/room")
    public ResponseEntity<HostelRoom> createRoom(@RequestBody HostelRoom room) {
        return ResponseEntity.ok(hostelService.createRoom(room));
    }

    @GetMapping("/rooms")
    public ResponseEntity<List<HostelRoom>> getAllRooms() {
        return ResponseEntity.ok(hostelService.getAllRooms());
    }

    @GetMapping("/rooms/{id}")
    public ResponseEntity<HostelRoom> getRoomById(@PathVariable Long id) {
        return ResponseEntity.ok(hostelService.getRoomById(id));
    }

    @PutMapping("/rooms/{id}")
    public ResponseEntity<HostelRoom> updateRoom(@PathVariable Long id, @RequestBody HostelRoom room) {
        return ResponseEntity.ok(hostelService.updateRoom(id, room));
    }

    @PatchMapping("/rooms/{id}")
    public ResponseEntity<HostelRoom> updateRoomPartial(@PathVariable Long id, @RequestBody HostelRoom room) {
        return ResponseEntity.ok(hostelService.updateRoomPartial(id, room));
    }

    @PatchMapping("/rooms/{id}/status")
    public ResponseEntity<HostelRoom> updateRoomStatus(@PathVariable Long id, @RequestParam HostelRoom.RoomStatus status) {
        return ResponseEntity.ok(hostelService.updateRoomStatus(id, status));
    }

    @DeleteMapping("/rooms/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        hostelService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }

    // ================= HOSTEL ATTENDANCE =================

    @PostMapping("/attendance")
    public ResponseEntity<HostelAttendance> markAttendance(@RequestBody HostelAttendance attendance) {
        return ResponseEntity.ok(hostelService.markAttendance(attendance));
    }

    @GetMapping("/attendances")
    public ResponseEntity<List<HostelAttendance>> getAllAttendance(@RequestParam(required = false) LocalDate date) {
        return ResponseEntity.ok(hostelService.getAllAttendance(date));
    }

    @GetMapping("/attendance/{id}")
    public ResponseEntity<HostelAttendance> getAttendanceById(@PathVariable Long id) {
        return ResponseEntity.ok(hostelService.getAttendanceById(id));
    }

    @PatchMapping("/attendance/{id}")
    public ResponseEntity<HostelAttendance> updateAttendancePartial(@PathVariable Long id, @RequestBody HostelAttendance attendance) {
        return ResponseEntity.ok(hostelService.updateAttendancePartial(id, attendance));
    }

    @DeleteMapping("/attendance/{id}")
    public ResponseEntity<Void> deleteAttendance(@PathVariable Long id) {
        hostelService.deleteAttendance(id);
        return ResponseEntity.noContent().build();
    }

    // ================= HOSTEL COMPLAINTS =================

    @PostMapping("/complaint")
    public ResponseEntity<HostelComplaint> addComplaint(@RequestBody HostelComplaint complaint) {
        return ResponseEntity.ok(hostelService.createComplaint(complaint));
    }

    @GetMapping("/complaints")
    public ResponseEntity<List<HostelComplaint>> getAllComplaints(@RequestParam(required = false) HostelComplaint.ComplaintStatus status) {
        return ResponseEntity.ok(hostelService.getAllComplaints(status));
    }

    @GetMapping("/complaint/{id}")
    public ResponseEntity<HostelComplaint> getComplaintById(@PathVariable Long id) {
        return ResponseEntity.ok(hostelService.getComplaintById(id));
    }

    @PutMapping("/complaint/{id}")
    public ResponseEntity<HostelComplaint> updateComplaintFull(@PathVariable Long id, @RequestBody HostelComplaint complaint) {
        return ResponseEntity.ok(hostelService.updateComplaintFull(id, complaint));
    }

    @PatchMapping("/complaint/{id}")
    public ResponseEntity<HostelComplaint> updateComplaint(
            @PathVariable Long id,
            @RequestParam(required = false) HostelComplaint.ComplaintStatus status,
            @RequestParam(required = false) String adminRemarks) {
        return ResponseEntity.ok(hostelService.updateComplaint(id, status, adminRemarks));
    }

    @DeleteMapping("/complaint/{id}")
    public ResponseEntity<Void> deleteComplaint(@PathVariable Long id) {
        hostelService.deleteComplaint(id);
        return ResponseEntity.noContent().build();
    }

    // ================= MESS MENU =================

    @PostMapping("/mess-menu")
    public ResponseEntity<MessDayMenu> createMenu(@RequestBody MessDayMenu menu) {
        return ResponseEntity.ok(hostelService.createMenu(menu));
    }

    @GetMapping("/mess-menus")
    public ResponseEntity<List<MessDayMenu>> getAllMenus() {
        return ResponseEntity.ok(hostelService.getAllMenus());
    }

    @GetMapping("/mess-menus/{id}")
    public ResponseEntity<MessDayMenu> getMenuById(@PathVariable Long id) {
        return ResponseEntity.ok(hostelService.getMenuById(id));
    }

    @PutMapping("/mess-menu/{id}")
    public ResponseEntity<MessDayMenu> updateMenuFull(@PathVariable Long id, @RequestBody MessDayMenu menu) {
        return ResponseEntity.ok(hostelService.updateMenu(id, menu));
    }

    @PatchMapping("/mess-menu/{id}")
    public ResponseEntity<MessDayMenu> updateMenu(@PathVariable Long id, @RequestBody MessDayMenu menu) {
        return ResponseEntity.ok(hostelService.updateMenuPartial(id, menu));
    }

    @DeleteMapping("/mess-menu/{id}")
    public ResponseEntity<Void> deleteMenu(@PathVariable Long id) {
        hostelService.deleteMenu(id);
        return ResponseEntity.noContent().build();
    }

    // ================= STUDENT HEALTH INCIDENTS =================

    @PostMapping({"/health", "/incident"})
    public ResponseEntity<StudentHealthIncident> createIncident(@RequestBody StudentHealthIncident incident) {
        return ResponseEntity.ok(hostelService.createIncident(incident));
    }

    @GetMapping({"/health", "/incidents"})
    public ResponseEntity<List<StudentHealthIncident>> getAllIncidents() {
        return ResponseEntity.ok(hostelService.getAllIncidents());
    }

    @GetMapping("/health/{id}")
    public ResponseEntity<StudentHealthIncident> getIncidentById(@PathVariable Long id) {
        return ResponseEntity.ok(hostelService.getIncidentById(id));
    }

    @PutMapping("/health/{id}")
    public ResponseEntity<StudentHealthIncident> updateIncidentFull(@PathVariable Long id, @RequestBody StudentHealthIncident incident) {
        return ResponseEntity.ok(hostelService.updateIncidentFull(id, incident));
    }

    @PatchMapping("/health/{id}")
    public ResponseEntity<StudentHealthIncident> updateIncident(
            @PathVariable Long id,
            @RequestParam(required = false) StudentHealthIncident.IncidentStatus status,
            @RequestParam(required = false) String clinicalNotes) {
        return ResponseEntity.ok(hostelService.updateIncident(id, status, clinicalNotes));
    }

    @DeleteMapping("/health/{id}")
    public ResponseEntity<Void> deleteIncident(@PathVariable Long id) {
        hostelService.deleteIncident(id);
        return ResponseEntity.noContent().build();
    }

    // ================= STUDENT HOSTEL ALLOCATION =================

    @PostMapping("/allocations")
    public ResponseEntity<StudentHostelAllocation> createAllocation(@RequestBody StudentHostelAllocation allocation) {
        return ResponseEntity.ok(hostelService.createAllocation(allocation));
    }

    @GetMapping("/allocations")
    public ResponseEntity<List<StudentHostelAllocation>> getAllAllocations(@RequestParam(required = false) StudentHostelAllocation.AllocationStatus status) {
        return ResponseEntity.ok(hostelService.getAllAllocations(status));
    }

    @GetMapping("/allocations/{id}")
    public ResponseEntity<StudentHostelAllocation> getAllocationById(@PathVariable Long id) {
        return ResponseEntity.ok(hostelService.getAllocationById(id));
    }

    @PutMapping("/allocations/{id}")
    public ResponseEntity<StudentHostelAllocation> updateAllocation(@PathVariable Long id, @RequestBody StudentHostelAllocation allocation) {
        return ResponseEntity.ok(hostelService.updateAllocation(id, allocation));
    }

    @PatchMapping("/allocations/{id}/status")
    public ResponseEntity<StudentHostelAllocation> updateAllocationStatus(
            @PathVariable Long id,
            @RequestParam StudentHostelAllocation.AllocationStatus status,
            @RequestParam(required = false) LocalDate leaveDate) {
        return ResponseEntity.ok(hostelService.updateAllocationStatus(id, status, leaveDate));
    }

    @DeleteMapping("/allocations/{id}")
    public ResponseEntity<Void> deleteAllocation(@PathVariable Long id) {
        hostelService.deleteAllocation(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/allocations/{id}/hard")
    public ResponseEntity<Void> hardDeleteAllocation(@PathVariable Long id) {
        hostelService.hardDeleteAllocation(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/roster")
    public ResponseEntity<List<StudentHostelAllocation>> getResidentRoster() {
        return ResponseEntity.ok(hostelService.getAllAllocations(StudentHostelAllocation.AllocationStatus.ACTIVE));
    }

    // ================= HOSTEL FEES =================

    @PostMapping("/fees")
    public ResponseEntity<StudentHostelFee> createFee(@RequestBody StudentHostelFee fee) {
        return ResponseEntity.ok(hostelService.createFee(fee));
    }

    @GetMapping("/fees")
    public ResponseEntity<List<StudentHostelFee>> getAllFees(@RequestParam(required = false) StudentHostelFee.FeeStatus status) {
        return ResponseEntity.ok(hostelService.getAllFees(status));
    }

    @GetMapping("/fees/{id}")
    public ResponseEntity<StudentHostelFee> getFeeById(@PathVariable Long id) {
        return ResponseEntity.ok(hostelService.getFeeById(id));
    }

    @PatchMapping("/fees/{id}/payment")
    public ResponseEntity<StudentHostelFee> updateFeePayment(@PathVariable Long id, @RequestParam Double amount) {
        return ResponseEntity.ok(hostelService.updatePayment(id, amount));
    }

    @PutMapping("/fees/{id}")
    public ResponseEntity<StudentHostelFee> updateFee(@PathVariable Long id, @RequestBody StudentHostelFee fee) {
        return ResponseEntity.ok(hostelService.updateFee(id, fee));
    }

    @DeleteMapping("/fees/{id}")
    public ResponseEntity<Void> deleteFee(@PathVariable Long id) {
        hostelService.cancelFee(id);
        return ResponseEntity.noContent().build();
    }

    // ================= STUDENT VISIT ENTRY =================

    @PostMapping("/visits")
    public ResponseEntity<StudentVisitEntry> createVisit(@RequestBody StudentVisitEntry visit) {
        return ResponseEntity.ok(hostelService.createVisit(visit));
    }

    @GetMapping("/visits")
    public ResponseEntity<List<StudentVisitEntry>> getAllVisits(@RequestParam(required = false) LocalDate date) {
        return ResponseEntity.ok(hostelService.getAllVisits(date));
    }

    @GetMapping("/visits/{id}")
    public ResponseEntity<StudentVisitEntry> getVisitById(@PathVariable Long id) {
        return ResponseEntity.ok(hostelService.getVisitById(id));
    }

    @PutMapping("/visits/{id}")
    public ResponseEntity<StudentVisitEntry> updateVisit(@PathVariable Long id, @RequestBody StudentVisitEntry visit) {
        return ResponseEntity.ok(hostelService.updateVisit(id, visit));
    }

    @PatchMapping("/visits/{id}/status")
    public ResponseEntity<StudentVisitEntry> updateVisitStatus(@PathVariable Long id, @RequestParam StudentVisitEntry.VisitStatus status) {
        return ResponseEntity.ok(hostelService.updateVisitStatus(id, status));
    }

    @DeleteMapping("/visits/{id}")
    public ResponseEntity<Void> deleteVisit(@PathVariable Long id) {
        hostelService.deleteVisit(id);
        return ResponseEntity.noContent().build();
    }
}
