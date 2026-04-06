package com.lms.www.campus.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lms.www.campus.Library.FineSlab;
import com.lms.www.campus.repository.Library.FineSlabRepository;

@Service
public class FineCalculationService {

    @Autowired
    private FineSlabRepository fineSlabRepository;

    public double calculateFine(Long settingId, int overdueDays) {
        if (overdueDays <= 0) {
            return 0.0;
        }
        if (settingId == null) {
            return 0.0;
        }

        List<FineSlab> slabs = fineSlabRepository.findByLibrarySettingsSettingIdOrderByFromDayAsc(settingId);

        if (slabs.isEmpty()) {
            return 0.0;
        }

        double totalFine = 0.0;
        int remainingDays = overdueDays;

        for (FineSlab slab : slabs) {
            if (remainingDays <= 0) {
                break;
            }

            // Calculate how many days fall in this slab range
            int slabStartDay = slab.getFromDay();
            int slabEndDay = slab.getToDay();

            // Skip if we haven't reached this slab yet
            if (overdueDays < slabStartDay) {
                continue;
            }

            // Calculate days that fall in this slab
            int daysInThisSlab;
            if (overdueDays >= slabEndDay) {
                // All days in this slab apply
                daysInThisSlab = slabEndDay - slabStartDay + 1;
            } else {
                // Only partial days apply
                daysInThisSlab = overdueDays - slabStartDay + 1;
            }

            // Calculate fine for this slab
            double slabFine = daysInThisSlab * slab.getFinePerDay();
            totalFine += slabFine;
        }

        return totalFine;
    }

    public double calculateFineForDateRange(Long settingId, java.time.LocalDate issueDate,
            java.time.LocalDate returnDate, int issueDurationDays) {
        if (returnDate == null || issueDate == null) {
            return 0.0;
        }

        java.time.LocalDate dueDate = issueDate.plusDays(issueDurationDays);

        if (returnDate.isAfter(dueDate)) {
            long overdueDays = java.time.temporal.ChronoUnit.DAYS.between(dueDate, returnDate);
            return calculateFine(settingId, (int) overdueDays);
        }

        return 0.0;
    }
}
