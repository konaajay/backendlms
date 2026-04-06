package com.lms.www.management.service;

import com.lms.www.management.model.AttendanceConfig;

public interface AttendanceConfigService {

    // ===============================
    // CREATE (only once per course+batch)
    // ===============================
    AttendanceConfig createConfig(AttendanceConfig config);

    // ===============================
    // GET CONFIG (for UI load)
    // ===============================
    AttendanceConfig getConfig(
            Long courseId,
            Long batchId
    );

    // ===============================
    // UPDATE CONFIG (toggles, sliders)
    // ===============================
    AttendanceConfig updateConfig(
            Long configId,
            AttendanceConfig updatedConfig
    );
}
