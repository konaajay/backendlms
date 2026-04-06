package com.lms.www.management.service;

import java.util.List;

import com.lms.www.management.model.AttendanceOfflineQueue;

public interface AttendanceOfflineQueueService {

    AttendanceOfflineQueue save(AttendanceOfflineQueue queue);

    List<AttendanceOfflineQueue> getByBatch(Long batchId);

    void sync();

    void delete(Long id);
}
