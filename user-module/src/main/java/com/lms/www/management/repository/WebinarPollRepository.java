package com.lms.www.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.www.management.model.WebinarPoll;

@Repository
public interface WebinarPollRepository extends JpaRepository<WebinarPoll, Long> {

    List<WebinarPoll> findByWebinar_WebinarIdOrderByCreatedAtAsc(Long webinarId);

}