package com.lms.www.management.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.www.management.enums.WebinarStatus;
import com.lms.www.management.model.Webinar;

@Repository
public interface WebinarRepository extends JpaRepository<Webinar, Long> {

    Page<Webinar> findByTrainerId(Long trainerId, Pageable pageable);

    List<Webinar> findByStatus(WebinarStatus status);

    List<Webinar> findByTrainerId(Long trainerId);

    List<Webinar> findByTrainerIdAndStatus(Long trainerId, WebinarStatus status);
}
