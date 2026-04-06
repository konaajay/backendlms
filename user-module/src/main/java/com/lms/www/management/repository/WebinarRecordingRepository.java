package com.lms.www.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.www.management.model.WebinarRecording;

@Repository
public interface WebinarRecordingRepository extends JpaRepository<WebinarRecording, Long> {

    List<WebinarRecording> findByWebinar_WebinarId(Long webinarId);

}
