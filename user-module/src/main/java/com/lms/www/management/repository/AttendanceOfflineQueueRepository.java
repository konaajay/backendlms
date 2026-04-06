package com.lms.www.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.www.management.model.AttendanceOfflineQueue;

@Repository
public interface AttendanceOfflineQueueRepository
        extends JpaRepository<AttendanceOfflineQueue, Long> {

    // 🔹 Get all unsynced offline records
    List<AttendanceOfflineQueue> findBySyncedFalse();

    // 🔹 Get offline records for a session
    List<AttendanceOfflineQueue> findBySessionIdAndSyncedFalse(Long sessionId);

    // 🔹 Get offline records for a batch
    List<AttendanceOfflineQueue> findByBatchIdAndSyncedFalse(Long batchId);
}
