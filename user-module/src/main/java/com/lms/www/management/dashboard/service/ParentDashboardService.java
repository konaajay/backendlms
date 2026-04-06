package com.lms.www.management.dashboard.service;

import java.util.List;
import com.lms.www.management.dashboard.dto.ChildSummaryDTO;
import com.lms.www.management.dashboard.dto.ParentDashboardDTO;
import com.lms.www.management.dashboard.dto.StudentDashboardDTO;

public interface ParentDashboardService {

    List<ChildSummaryDTO> getChildrenForParent(Long parentId);

    ParentDashboardDTO getParentDashboard(Long parentId);

    StudentDashboardDTO getSingleChildDashboard(Long parentId, Long studentId);

}
