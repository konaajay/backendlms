package com.lms.www.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.www.management.model.WebinarChatMessage;

@Repository
public interface WebinarChatMessageRepository extends JpaRepository<WebinarChatMessage, Long> {

    List<WebinarChatMessage> findByWebinar_WebinarIdOrderBySentAtAsc(Long webinarId);

}
