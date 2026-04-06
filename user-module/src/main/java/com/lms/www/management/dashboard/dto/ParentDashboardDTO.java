package com.lms.www.management.dashboard.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParentDashboardDTO {

    private Long parentUserId;
    private List<ChildDashboard> children;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChildDashboard {
        private Long studentId;
        private String studentName;
        private StudentDashboardDTO dashboard;
    }
}
