package com.lms.www.management.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lms.www.management.model.SessionFeedback;

@Repository
public interface SessionFeedbackRepository extends JpaRepository<SessionFeedback, Long> {

    Optional<SessionFeedback> findBySessionIdAndFromUserIdAndToUserId(Long sessionId, Long fromUserId, Long toUserId);

    // Using COALESCE to return 0.0 if there is no feedback yet, preventing null
    // returns
    @Query("SELECT COALESCE(AVG(f.rating), 0.0) FROM SessionFeedback f WHERE f.toUserId = :toUserId")
    Double getAverageRatingForUser(@Param("toUserId") Long toUserId);

    Long countByToUserId(Long toUserId);
}