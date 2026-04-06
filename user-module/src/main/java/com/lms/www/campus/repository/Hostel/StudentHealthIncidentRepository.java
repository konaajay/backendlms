package com.lms.www.campus.repository.Hostel;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.www.campus.Hostel.StudentHealthIncident;

@Repository
public interface StudentHealthIncidentRepository
        extends JpaRepository<StudentHealthIncident, Long> {

    List<StudentHealthIncident> findByIsDeletedFalse();

    List<StudentHealthIncident> findByStudentIdAndIsDeletedFalse(Long studentId);
}