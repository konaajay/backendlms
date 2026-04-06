package com.lms.www.management.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lms.www.management.model.StudentVideoProgress;

public interface StudentVideoProgressRepository extends JpaRepository<StudentVideoProgress, Long> {

    Optional<StudentVideoProgress> findByStudentIdAndSessionContentId(
            Long studentId,
            Long sessionContentId);

    List<StudentVideoProgress> findByStudentIdAndSessionId(
            Long studentId,
            Long sessionId);

    @Query("""
        SELECT COALESCE(SUM(svp.watchedDuration), 0)
        FROM StudentVideoProgress svp
        WHERE svp.studentId = :studentId
        AND svp.sessionId = :sessionId
    """)
    Long getTotalWatchedDurationByStudentAndSession(
            @Param("studentId") Long studentId,
            @Param("sessionId") Long sessionId);

    @Query("""
        SELECT COUNT(svp)
        FROM StudentVideoProgress svp
        WHERE svp.studentId = :studentId
        AND svp.sessionId = :sessionId
        AND svp.status = 'COMPLETED'
    """)
    Long countCompletedVideos(
            @Param("studentId") Long studentId,
            @Param("sessionId") Long sessionId);
    
    //List<StudentVideoProgress> findByUserIdAndSessionSessionId(Long userId, Long sessionId);
    
    List<StudentVideoProgress> findByUserIdAndSessionId(Long userId, Long sessionId);
    
    
}