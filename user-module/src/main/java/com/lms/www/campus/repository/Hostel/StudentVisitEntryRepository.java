package com.lms.www.campus.repository.Hostel;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.www.campus.Hostel.StudentVisitEntry;

@Repository
public interface StudentVisitEntryRepository
        extends JpaRepository<StudentVisitEntry, Long> {

    List<StudentVisitEntry> findByVisitDate(LocalDate visitDate);

    List<StudentVisitEntry> findByStudentId(Long studentId);
}