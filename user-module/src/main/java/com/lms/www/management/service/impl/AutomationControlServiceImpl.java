package com.lms.www.management.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.lms.www.management.model.AutomationControl;
import com.lms.www.management.repository.AutomationControlRepository;
import com.lms.www.management.service.AutomationControlService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AutomationControlServiceImpl implements AutomationControlService {

    private final AutomationControlRepository automationControlRepository;

    @Override
    public List<AutomationControl> getAllControls() {
        return automationControlRepository.findAll();
    }

    @Override
    public AutomationControl getControl(String ruleName) {
        return automationControlRepository.findByRuleName(ruleName)
                .orElseGet(() -> {
                    return automationControlRepository.save(AutomationControl.builder()
                            .ruleName(ruleName)
                            .isEnabled(false)
                            .actionCount(0)
                            .build());
                });
    }

    @Override
    public AutomationControl toggleControl(String ruleName) {
        AutomationControl control = getControl(ruleName);
        Boolean currentVal = control.getIsEnabled();
        control.setIsEnabled(currentVal == null ? true : !currentVal);
        return automationControlRepository.save(control);
    }

    @Override
    public void updateLastRunAndActions(String ruleName, int newActions) {
        AutomationControl control = getControl(ruleName);
        control.setLastRunDate(LocalDateTime.now());
        Integer currentCount = control.getActionCount();
        control.setActionCount((currentCount != null ? currentCount : 0) + newActions);
        automationControlRepository.save(control);
    }
}
