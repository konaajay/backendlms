package com.lms.www.fee.service;

import com.lms.www.fee.dto.AccountCreateRequest;
import com.lms.www.fee.allocation.entity.StudentFeeAllocation;
import com.lms.www.fee.allocation.repository.StudentFeeAllocationRepository;
import com.lms.www.model.User;
import com.lms.www.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeeAccountService {

    private final UserRepository userRepository;
    private final StudentFeeAllocationRepository allocationRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User createAccount(AccountCreateRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Account with email " + request.getEmail() + " already exists.");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getName());
        user.setRoleName(request.getRole() != null ? request.getRole() : "ROLE_USER");
        user.setEnabled(true);

        user.setPhone("0000000000"); // Dummy for requirement compliance

        User savedUser = userRepository.save(user);

        if (request.getFeeAmount() != null) {
            allocateFee(savedUser, request);
        }

        return savedUser;
    }

    private void allocateFee(User user, AccountCreateRequest request) {
        StudentFeeAllocation allocation = StudentFeeAllocation.builder()
                .userId(user.getUserId())
                .studentEmail(user.getEmail())
                .studentName(user.getFirstName())
                .feeStructureId(request.getFeeStructureId() != null ? request.getFeeStructureId() : 0L)
                .courseId(request.getCourseId() != null ? request.getCourseId() : 0L)
                .batchId(request.getBatchId() != null ? request.getBatchId() : 0L)
                .originalAmount(request.getFeeAmount())
                .payableAmount(request.getFeeAmount())
                .remainingAmount(request.getFeeAmount())
                .installmentCount(1)
                .durationMonths(1)
                .build();
        allocationRepository.save(allocation);
    }

    public Object getAccountDetails(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<StudentFeeAllocation> allocations = allocationRepository.findByUserId(userId);
        StudentFeeAllocation allocation = allocations.isEmpty() ? null : allocations.get(0);

        java.util.Map<String, Object> feeInfo = null;
        if (allocation != null) {
            feeInfo = java.util.Map.of(
                    "feeAmount", allocation.getPayableAmount(),
                    "paidAmount", allocation.getAdvancePayment(),
                    "dueAmount", allocation.getRemainingAmount(),
                    "status", allocation.getStatus());
        }

        return java.util.Map.of(
                "profile", user,
                "fee", feeInfo != null ? feeInfo : java.util.Collections.emptyMap());
    }
}
