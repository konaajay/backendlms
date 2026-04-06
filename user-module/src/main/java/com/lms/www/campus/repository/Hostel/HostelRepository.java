package com.lms.www.campus.repository.Hostel;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.www.campus.Hostel.Hostel;

@Repository
public interface HostelRepository extends JpaRepository<Hostel, Long> {

    List<Hostel> findByIsDeletedFalse();

    Optional<Hostel> findByHostelName(String hostelName);

    boolean existsByHostelNameAndIsDeletedFalse(String hostelName);
}
