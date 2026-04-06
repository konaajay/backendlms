package com.lms.www.campus.repository.Hostel;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.www.campus.Hostel.Hostel;
import com.lms.www.campus.Hostel.HostelComplaint;
import com.lms.www.campus.Hostel.HostelRoom;

@Repository
public interface HostelComplaintRepository extends JpaRepository<HostelComplaint, Long> {

    List<HostelComplaint> findByStudentId(Long studentId);

    
    List<HostelComplaint> findByStatus(HostelComplaint.ComplaintStatus status);
    
    Optional<Hostel> findByHostelName(String hostelName);
    
    Optional<HostelRoom> findByRoomNumber(String roomNumber);
}