package com.lms.www.management.service;

import java.util.List;

import com.lms.www.management.model.Approval;

public interface ApprovalService {

    List<Approval> getPendingApprovals();

    Approval approve(Long id);

    Approval reject(Long id);

    Approval createApproval(Approval approval); // ✅ added (important)
}