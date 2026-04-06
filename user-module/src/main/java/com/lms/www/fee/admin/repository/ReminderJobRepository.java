package com.lms.www.fee.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.lms.www.fee.admin.entity.ReminderJob;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReminderJobRepository extends JpaRepository<ReminderJob, Long> {
    boolean existsByInstallmentIdAndReminderOffset(Long installmentId, Integer reminderOffset);

    @Query("SELECT r FROM ReminderJob r WHERE r.status = :status AND r.scheduledDate <= CURRENT_DATE")
    List<ReminderJob> findPendingJobs(@Param("status") ReminderJob.JobStatus status);

    @Query("SELECT r FROM ReminderJob r WHERE r.status = :status AND r.retryCount < 3 AND r.nextRetryTime <= :now")
    List<ReminderJob> findJobsToRetry(@Param("now") LocalDateTime now, @Param("status") ReminderJob.JobStatus status);
}
