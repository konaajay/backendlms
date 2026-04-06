package com.lms.www.management.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.lms.www.management.model.Approval;
import com.lms.www.management.repository.ApprovalRepository;
import com.lms.www.management.service.ApprovalService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApprovalServiceImpl implements ApprovalService {

    private final ApprovalRepository repo;

    @Override
    public List<Approval> getPendingApprovals() {
        return repo.findByStatus("PENDING");
    }

    @Override
    public Approval createApproval(Approval approval) {

        // defaults
        if (approval.getStatus() == null) {
            approval.setStatus("PENDING");
        }

        approval.setCreatedAt(LocalDateTime.now());
        approval.setUpdatedAt(LocalDateTime.now());

        return repo.save(approval);
    }

    @Override
    public Approval approve(Long id) {

        Approval approval = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Approval not found"));

        approval.setStatus("APPROVED");
        approval.setUpdatedAt(LocalDateTime.now());

        return repo.save(approval);
    }

    @Override
    public Approval reject(Long id) {

        Approval approval = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Approval not found"));

        approval.setStatus("REJECTED");
        approval.setUpdatedAt(LocalDateTime.now());

        return repo.save(approval);
    }
}