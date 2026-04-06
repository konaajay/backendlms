package com.lms.www.management.service;

import java.util.List;

import com.lms.www.management.model.AutomationControl;

public interface AutomationControlService {
    List<AutomationControl> getAllControls();

    AutomationControl toggleControl(String ruleName);

    AutomationControl getControl(String ruleName);

    void updateLastRunAndActions(String ruleName, int newActions);
}