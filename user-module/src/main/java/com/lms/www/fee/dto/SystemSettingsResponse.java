package com.lms.www.fee.dto;


import java.util.List;

public class SystemSettingsResponse {
    private Integer paymentLinkDays;
    private List<Integer> reminderOffsets;

    // Manual Getters and Setters
    public Integer getPaymentLinkDays() { return paymentLinkDays; }
    public void setPaymentLinkDays(Integer paymentLinkDays) { this.paymentLinkDays = paymentLinkDays; }

    public List<Integer> getReminderOffsets() { return reminderOffsets; }
    public void setReminderOffsets(List<Integer> reminderOffsets) { this.reminderOffsets = reminderOffsets; }
}
