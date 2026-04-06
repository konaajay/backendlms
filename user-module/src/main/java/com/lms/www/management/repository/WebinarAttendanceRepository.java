package com.lms.www.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lms.www.management.model.WebinarAttendance;

@Repository
public interface WebinarAttendanceRepository extends JpaRepository<WebinarAttendance, Long> {

    List<WebinarAttendance> findByWebinar_WebinarId(Long webinarId);

    List<WebinarAttendance> findByRegistration_RegistrationId(Long registrationId);

    @Query("SELECT wa FROM WebinarAttendance wa JOIN FETCH wa.registration WHERE wa.webinar.webinarId = :webinarId")
    List<WebinarAttendance> findByWebinarIdWithRegistration(@Param("webinarId") Long webinarId);
}
