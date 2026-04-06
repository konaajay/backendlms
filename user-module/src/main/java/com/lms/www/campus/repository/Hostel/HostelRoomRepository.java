package com.lms.www.campus.repository.Hostel;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.www.campus.Hostel.HostelRoom;

@Repository
public interface HostelRoomRepository extends JpaRepository<HostelRoom, Long> {

	List<HostelRoom> findByIsDeletedFalse();

	Optional<HostelRoom> findByRoomNumber(String roomNumber);
}