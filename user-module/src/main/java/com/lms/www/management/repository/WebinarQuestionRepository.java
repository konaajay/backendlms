package com.lms.www.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lms.www.management.model.WebinarQuestion;

@Repository
public interface WebinarQuestionRepository extends JpaRepository<WebinarQuestion, Long> {

    List<WebinarQuestion> findByWebinar_WebinarIdOrderByAskedAtAsc(Long webinarId);

    @Query("SELECT wq FROM WebinarQuestion wq JOIN FETCH wq.webinar WHERE wq.webinar.webinarId = :webinarId")
    List<WebinarQuestion> findQuestionsWithWebinarFetched(@Param("webinarId") Long webinarId);

}