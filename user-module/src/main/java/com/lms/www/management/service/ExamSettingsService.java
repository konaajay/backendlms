package com.lms.www.management.service;

import com.lms.www.management.model.ExamSettings;

public interface ExamSettingsService {

    ExamSettings saveSettings(Long examId, ExamSettings settings);

    ExamSettings getSettingsByExamId(Long examId);

    // 🔥 Toggle MCQ option shuffle
    ExamSettings updateShuffleOptions(Long examId, Boolean shuffle);
}
