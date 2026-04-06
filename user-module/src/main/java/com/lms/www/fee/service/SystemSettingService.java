package com.lms.www.fee.service;

import com.lms.www.fee.admin.entity.MasterSetting;
import com.lms.www.fee.enums.MasterSettingType;
import com.lms.www.fee.admin.repository.MasterSettingRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SystemSettingService {

    private static final String KEY_REMINDER_OFFSETS = "payment.link.reminder_days";
    private static final String DEFAULT_OFFSETS = "-3,0,2";

    private final MasterSettingRepository repo;

    public List<Integer> getReminderOffsets() {
        String csv = repo.findByTypeAndKeyNameAndActiveTrue(MasterSettingType.SYSTEM, KEY_REMINDER_OFFSETS)
                .map(MasterSetting::getValue)
                .orElse(DEFAULT_OFFSETS);
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    public void setReminderOffsets(List<Integer> offsets) {
        String csv = offsets.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        MasterSetting setting = repo.findByTypeAndKeyName(MasterSettingType.SYSTEM, KEY_REMINDER_OFFSETS)
                .orElse(new MasterSetting());

        setting.setType(MasterSettingType.SYSTEM);
        setting.setKeyName(KEY_REMINDER_OFFSETS);
        setting.setValue(csv);
        setting.setDescription("Comma-separated reminder offsets (e.g., -3,0,2)");
        setting.setActive(true);

        repo.save(setting);
    }

    public void setPaymentLinkDaysBeforeDue(int days) {
        // Compatibility wrapper: just set it as a single negative offset if it was
        // called
        setReminderOffsets(List.of(-days, 0));
    }

    public int getPaymentLinkDaysBeforeDue() {
        // Legacy fallback: first negative offset found
        return getReminderOffsets().stream()
                .filter(o -> o < 0)
                .mapToInt(Integer::intValue)
                .map(Math::abs)
                .findFirst()
                .orElse(3);
    }
}
